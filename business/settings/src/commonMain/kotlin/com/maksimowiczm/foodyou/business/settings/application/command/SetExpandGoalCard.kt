package com.maksimowiczm.foodyou.business.settings.application.command

import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result

data class SetExpandGoalCardCommand(val newValue: Boolean) : Command<Unit, Unit>

internal class SetExpandGoalCardCommandHandler(private val localSettings: LocalSettingsDataSource) :
    CommandHandler<SetExpandGoalCardCommand, Unit, Unit> {

    override suspend fun handle(command: SetExpandGoalCardCommand): Result<Unit, Unit> {
        localSettings.updateExpandGoalCard(command.newValue)
        return Ok(Unit)
    }
}
