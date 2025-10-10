package com.maksimowiczm.foodyou.app.ui.onboarding

import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.analytics.application.AppLaunchCommand
import com.maksimowiczm.foodyou.analytics.application.AppLaunchCommandHandler
import com.maksimowiczm.foodyou.app.domain.AppConfig
import com.maksimowiczm.foodyou.common.LocalAccountId
import com.maksimowiczm.foodyou.device.domain.DeviceRepository

class CreatePrimaryAccountUseCase(
    private val accountRepository: AccountRepository,
    private val deviceRepository: DeviceRepository,
    private val accountManager: AccountManager,
    private val appLaunchCommandHandler: AppLaunchCommandHandler,
    private val appConfig: AppConfig,
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
        appLaunchCommandHandler.handle(
            AppLaunchCommand(
                localAccountId = account.localAccountId,
                versionName = appConfig.versionName,
            )
        )

        return account.localAccountId
    }
}

private fun UiAvatar.toProfileAvatar(): Profile.Avatar =
    when (this) {
        UiAvatar.PERSON -> Profile.Avatar.PERSON
        UiAvatar.WOMAN -> Profile.Avatar.WOMAN
        UiAvatar.MAN -> Profile.Avatar.MAN
        UiAvatar.ENGINEER -> Profile.Avatar.ENGINEER
    }
