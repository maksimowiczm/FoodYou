package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.LocalFoodPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result

data class UpdateUseUsda(val useUsda: Boolean) : Command<Unit, Unit>

internal class UpdateUseUsdaCommandHandler(
    private val localFoodPreferences: LocalFoodPreferencesDataSource
) : CommandHandler<UpdateUseUsda, Unit, Unit> {

    override suspend fun handle(command: UpdateUseUsda): Result<Unit, Unit> {
        localFoodPreferences.updateUsdaEnabled(command.useUsda)
        return Ok(Unit)
    }
}
