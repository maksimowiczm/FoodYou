package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.analytics.application.AppLaunchUseCase
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import com.maksimowiczm.foodyou.app.ui.common.component.ProfileAvatarMapper
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchPreferencesRepository
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
    private val accountManager: AppAccountManager,
    private val appLaunchUseCase: AppLaunchUseCase,
    private val foodSearchPreferencesRepository: FoodSearchPreferencesRepository,
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
                val profile = Profile.new(name = name, avatar = ProfileAvatarMapper.toModel(avatar))
                val account = Account.create(primaryProfile = profile)

                val searchPreferences =
                    foodSearchPreferencesRepository
                        .observe()
                        .first()
                        .copy(
                            allowOpenFoodFacts = allowOpenFoodFacts,
                            allowFoodDataCentralUSDA = allowFoodDataCentral,
                        )

                accountRepository.save(account)
                accountManager.setAppAccountId(account.localAccountId)
                accountManager.setAppProfileId(profile.id)
                appLaunchUseCase.execute(account.localAccountId)
                foodSearchPreferencesRepository.save(searchPreferences)

                account.localAccountId
            }
            val minDelayTask = async { delay(2_000) }

            val localAccountId = realTask.await()
            minDelayTask.await()

            eventBus.send(OnboardingEvent.Finished(localAccountId))
        }
    }
}
