package com.maksimowiczm.foodyou.business.settings.application.command

import com.maksimowiczm.foodyou.business.settings.domain.HomeCard
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.business.shared.domain.error.ErrorLoggingUtils
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result

data class SetHomeCardOrderCommand(val order: List<HomeCard>) :
    Command<Unit, SetHomeCardOrderError>

sealed interface SetHomeCardOrderError {
    data object MissingHomeCard : SetHomeCardOrderError
}

internal class SetHomeCardOrderCommandHandler(private val localSettings: LocalSettingsDataSource) :
    CommandHandler<SetHomeCardOrderCommand, Unit, SetHomeCardOrderError> {

    override suspend fun handle(
        command: SetHomeCardOrderCommand
    ): Result<Unit, SetHomeCardOrderError> {
        if (command.order.distinct().size != HomeCard.entries.size) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = SetHomeCardOrderError.MissingHomeCard,
                message = { "Not all home cards are present in the order: ${command.order}" },
            )
        }

        localSettings.updateHomeCardOrder(command.order)
        return Ok(Unit)
    }

    private companion object {
        private val TAG = "SetHomeCardOrderCommandHandler"
    }
}
