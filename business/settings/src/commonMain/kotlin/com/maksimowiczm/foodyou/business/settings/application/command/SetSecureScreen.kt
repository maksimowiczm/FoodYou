package com.maksimowiczm.foodyou.business.settings.application.command

import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result

data class SetSecureScreenCommand(val secureScreen: Boolean) : Command<Unit, Unit>

internal class SetSecureScreenCommandHandler(private val localSettings: LocalSettingsDataSource) :
    CommandHandler<SetSecureScreenCommand, Unit, Unit> {

    override suspend fun handle(command: SetSecureScreenCommand): Result<Unit, Unit> {
        localSettings.updateSecureScreen(command.secureScreen)
        return Ok(Unit)
    }
}
