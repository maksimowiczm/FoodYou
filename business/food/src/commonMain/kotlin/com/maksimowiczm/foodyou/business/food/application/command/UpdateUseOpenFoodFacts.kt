package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.LocalFoodPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlin.reflect.KClass

data class UpdateUseOpenFoodFactsCommand(val useOpenFoodFacts: Boolean) : Command

internal class UpdateUseOpenFoodFactsCommandHandler(
    private val localFoodPreferences: LocalFoodPreferencesDataSource
) : CommandHandler<UpdateUseOpenFoodFactsCommand, Unit, Unit> {
    override val commandType: KClass<UpdateUseOpenFoodFactsCommand>
        get() = UpdateUseOpenFoodFactsCommand::class

    override suspend fun handle(command: UpdateUseOpenFoodFactsCommand): Result<Unit, Unit> {
        localFoodPreferences.updateOpenFoodFactsEnabled(command.useOpenFoodFacts)
        return Ok(Unit)
    }
}
