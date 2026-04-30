package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchPreferencesRepository
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class OnboardingViewModel(
    private val accountRepository: AccountRepository,
    private val accountManager: AppProfileManager,
    private val foodSearchPreferencesRepository: FoodSearchPreferencesRepository,
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
                val account = Account(profiles = listOf(profile))

                val searchPreferences =
                    foodSearchPreferencesRepository
                        .observe()
                        .first()
                        .copy(
                            allowOpenFoodFacts = allowOpenFoodFacts,
                            allowFoodDataCentralUSDA = allowFoodDataCentral,
                        )

                accountRepository.save(account)
                accountManager.setAppProfileId(profile.id)
                foodSearchPreferencesRepository.save(searchPreferences)
            }
            val minDelayTask = async { delay(2_000) }

            realTask.await()
            minDelayTask.await()

            eventBus.send(OnboardingEvent.Finished)
        }
    }
}
