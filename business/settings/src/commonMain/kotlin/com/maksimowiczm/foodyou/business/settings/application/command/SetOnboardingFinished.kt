package com.maksimowiczm.foodyou.business.settings.application.command

import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result

data class SetOnboardingFinishedCommand(val onboardingFinished: Boolean) : Command<Unit, Unit>

internal class SetOnboardingFinishedCommandHandler(
    private val localSettings: LocalSettingsDataSource
) : CommandHandler<SetOnboardingFinishedCommand, Unit, Unit> {
    override suspend fun handle(command: SetOnboardingFinishedCommand): Result<Unit, Unit> {
        localSettings.updateOnboardingFinished(command.onboardingFinished)
        return Ok(Unit)
    }
}
