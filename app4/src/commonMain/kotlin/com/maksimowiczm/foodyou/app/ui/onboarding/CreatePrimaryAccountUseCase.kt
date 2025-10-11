package com.maksimowiczm.foodyou.app.ui.onboarding

import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.analytics.application.AppLaunchUseCase
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.common.LocalAccountId
import com.maksimowiczm.foodyou.device.domain.DeviceRepository

class CreatePrimaryAccountUseCase(
    private val accountRepository: AccountRepository,
    private val deviceRepository: DeviceRepository,
    private val accountManager: AccountManager,
    private val appLaunchUseCase: AppLaunchUseCase,
) {
    suspend fun execute(uiState: OnboardingUiState): LocalAccountId {
        val device = deviceRepository.load()

        device.updatePrivacySettings {
            it.copy(foodYouServicesAllowed = uiState.allowFoodYouServices)
        }

        val profile =
            Profile.new(name = uiState.profileName, avatar = uiState.avatar.toProfileAvatar())
        val account = Account.create(primaryProfile = profile)

        // TODO
        //  Setup search privacy settings based on user choice once food search is implemented

        accountRepository.save(account)
        deviceRepository.save(device)
        accountManager.setPrimaryAccountId(account.localAccountId)
        appLaunchUseCase.execute(account.localAccountId)

        return account.localAccountId
    }
}

private fun UiProfileAvatar.toProfileAvatar(): Profile.Avatar =
    when (this) {
        UiProfileAvatar.PERSON -> Profile.Avatar.PERSON
        UiProfileAvatar.WOMAN -> Profile.Avatar.WOMAN
        UiProfileAvatar.MAN -> Profile.Avatar.MAN
        UiProfileAvatar.ENGINEER -> Profile.Avatar.ENGINEER
    }
