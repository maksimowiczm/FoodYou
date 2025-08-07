package com.maksimowiczm.foodyou.business.settings.application.command

import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlin.reflect.KClass

data class UpdateSettingsCommand(val settings: Settings) : Command

internal class UpdateSettingsCommandHandler(
    private val settingsDataSource: LocalSettingsDataSource
) : CommandHandler<UpdateSettingsCommand, Unit, Unit> {
    override val commandType: KClass<UpdateSettingsCommand>
        get() = UpdateSettingsCommand::class

    override suspend fun handle(command: UpdateSettingsCommand): Result<Unit, Unit> {
        settingsDataSource.update(command.settings)
        return Ok(Unit)
    }
}
