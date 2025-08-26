package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.LocalFoodPreferencesDataSource
import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result

data class UpdateUsdaApiKeyCommand(val apiKey: String? = null) : Command<Unit, Unit>

internal class UpdateUsdaApiKeyCommandHandler(
    private val localFoodPreferences: LocalFoodPreferencesDataSource
) : CommandHandler<UpdateUsdaApiKeyCommand, Unit, Unit> {

    override suspend fun handle(command: UpdateUsdaApiKeyCommand): Result<Unit, Unit> {
        localFoodPreferences.updateUsdaApiKey(command.apiKey)
        return Ok(Unit)
    }
}
