package com.maksimowiczm.foodyou.business.settings.application.command

import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlin.reflect.KClass

data class SetLastRememberedVersionCommand(val newValue: String) : Command

internal class SetLastRememberedVersionCommandHandler(
    private val localSettings: LocalSettingsDataSource
) : CommandHandler<SetLastRememberedVersionCommand, Unit, Unit> {
    override val commandType: KClass<SetLastRememberedVersionCommand>
        get() = SetLastRememberedVersionCommand::class

    override suspend fun handle(command: SetLastRememberedVersionCommand): Result<Unit, Unit> {
        localSettings.updateLastRememberedVersion(command.newValue)
        return Ok(Unit)
    }
}
