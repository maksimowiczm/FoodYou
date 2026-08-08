package com.maksimowiczm.foodyou.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.account.domain.addProfile
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import com.maksimowiczm.foodyou.mealplan.domain.MealTemplates
import com.maksimowiczm.foodyou.mealplan.domain.initialize
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.shared.ui.component.UiProfileAvatar
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class OnboardingViewModel(
    private val accountService: AccountService,
    private val accountManager: AppProfileManager,
    private val openFoodFacts: OpenFoodFactsSettingsRepository,
    private val foodDataCentral: FoodDataCentralSettingsRepository,
    private val blobStorage: BlobStorage,
    private val mealPlanService: MealPlanService,
) : ViewModel() {
    private val _finishingOnboarding = MutableStateFlow(false)
    val finishingOnboarding = _finishingOnboarding.asStateFlow()

    val isOpenFoodFactsSignedIn =
        openFoodFacts
            .observe()
            .map { it.credentials != null }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = runBlocking { openFoodFacts.observe().first().credentials != null },
            )

    private val eventBus = Channel<OnboardingEvent>()
    val events = eventBus.receiveAsFlow()

    fun signOutFromOpenFoodFacts() {
        viewModelScope.launch { openFoodFacts.update { it.copy(credentials = null) } }
    }

    fun finishOnboarding(
        name: String,
        avatar: UiProfileAvatar,
        allowOpenFoodFacts: Boolean,
        allowFoodDataCentral: Boolean,
        language: Language,
    ) {
        viewModelScope.launch {
            _finishingOnboarding.value = true

            val realTask = async {
                val profileAvatar =
                    when (avatar) {
                        is UiProfileAvatar.Predefined -> Profile.Avatar.Predefined(avatar.variant)
                        is UiProfileAvatar.Uri -> {
                            val file = PlatformFile(avatar.uri.value)
                            val digest = blobStorage.store(file.readBytes())
                            Profile.Avatar.Photo(digest)
                        }
                    }

                val profile = Profile(name = name, avatar = profileAvatar)

                awaitAll(
                    async {
                        accountService.update { addProfile(profile) }
                        accountManager.setAppProfileId(profile.id)
                    },
                    async { openFoodFacts.update { it.copy(remoteEnabled = allowOpenFoodFacts) } },
                    async {
                        foodDataCentral.update { it.copy(remoteEnabled = allowFoodDataCentral) }
                    },
                    async {
                        mealPlanService.transact {
                            it.initialize(language, MealTemplates.forLanguage(language))
                        }
                    },
                )
            }
            val minDelayTask = async { delay(2_000.milliseconds) }

            realTask.await()
            minDelayTask.await()

            eventBus.send(OnboardingEvent.Finished)
        }
    }
}
