package com.maksimowiczm.foodyou.app.ui.onboarding

import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.analytics.application.AppLaunchUseCase
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import com.maksimowiczm.foodyou.app.ui.common.component.ProfileAvatarMapper
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchPreferencesRepository
import kotlinx.coroutines.flow.first

class CreatePrimaryAccountUseCase(
    private val accountRepository: AccountRepository,
    private val accountManager: AppAccountManager,
    private val appLaunchUseCase: AppLaunchUseCase,
    private val foodSearchPreferencesRepository: FoodSearchPreferencesRepository,
) {
    suspend fun execute(uiState: OnboardingUiState): LocalAccountId {
        val profile =
            Profile.new(
                name = uiState.profileName,
                avatar = ProfileAvatarMapper.toModel(uiState.avatar),
            )
        val account = Account.create(primaryProfile = profile)

        val searchPreferences =
            foodSearchPreferencesRepository
                .observe()
                .first()
                .copy(
                    allowOpenFoodFacts = uiState.allowOpenFoodFacts,
                    allowFoodDataCentralUSDA = uiState.allowFoodDataCentral,
                )

        accountRepository.save(account)
        accountManager.setAppAccountId(account.localAccountId)
        accountManager.setAppProfileId(profile.id)
        appLaunchUseCase.execute(account.localAccountId)
        foodSearchPreferencesRepository.save(searchPreferences)

        return account.localAccountId
    }
}
