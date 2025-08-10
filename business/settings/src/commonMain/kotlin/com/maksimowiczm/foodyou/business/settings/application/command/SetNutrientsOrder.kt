package com.maksimowiczm.foodyou.business.settings.application.command

import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.business.shared.domain.error.ErrorLoggingUtils
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result

data class SetNutrientsOrderCommand(val order: List<NutrientsOrder>) :
    Command<Unit, SetNutrientsOrderError>

sealed interface SetNutrientsOrderError {
    data object NutrientsMissing : SetNutrientsOrderError
}

internal class SetNutrientsOrderCommandHandler(private val localSettings: LocalSettingsDataSource) :
    CommandHandler<SetNutrientsOrderCommand, Unit, SetNutrientsOrderError> {

    override suspend fun handle(
        command: SetNutrientsOrderCommand
    ): Result<Unit, SetNutrientsOrderError> {
        if (command.order.distinct().size != NutrientsOrder.entries.size) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = SetNutrientsOrderError.NutrientsMissing,
                message = { "Nutrients order is missing some nutrients." },
            )
        }

        localSettings.updateNutrientsOrder(command.order)
        return Ok(Unit)
    }

    private companion object {
        private val TAG = "SetNutrientsOrderCommandHandler"
    }
}
