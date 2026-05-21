package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.account.domain.addProfile
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class OnboardingViewModel(
    private val accountService: AccountService,
    private val accountManager: AppProfileManager,
    private val openFoodFacts: OpenFoodFactsSettingsRepository,
    private val foodDataCentral: FoodDataCentralSettingsRepository,
    private val blobStorage: BlobStorage,
) : ViewModel() {
    private val _finishingOnboarding = MutableStateFlow(false)
    val finishingOnboarding = _finishingOnboarding.asStateFlow()

    private val eventBus = Channel<OnboardingEvent>()
    val events = eventBus.receiveAsFlow()

    fun finishOnboarding(
        name: String,
        avatar: UiProfileAvatar,
        allowOpenFoodFacts: Boolean,
        allowFoodDataCentral: Boolean,
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
                )
            }
            val minDelayTask = async { delay(2_000) }

            realTask.await()
            minDelayTask.await()

            eventBus.send(OnboardingEvent.Finished)
        }
    }
}
