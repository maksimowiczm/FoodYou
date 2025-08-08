package com.maksimowiczm.foodyou.business.settings.application.command

import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlin.reflect.KClass

data class SetTranslationWarningCommand(val newValue: Boolean) : Command

internal class SetTranslationWarningCommandHandler(
    private val localSettings: LocalSettingsDataSource
) : CommandHandler<SetTranslationWarningCommand, Unit, Unit> {
    override val commandType: KClass<SetTranslationWarningCommand>
        get() = SetTranslationWarningCommand::class

    override suspend fun handle(command: SetTranslationWarningCommand): Result<Unit, Unit> {
        localSettings.updateShowTranslationWarning(command.newValue)
        return Ok(Unit)
    }
}
