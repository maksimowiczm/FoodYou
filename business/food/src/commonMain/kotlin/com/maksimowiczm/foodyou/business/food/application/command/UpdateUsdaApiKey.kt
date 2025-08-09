package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.LocalFoodPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlin.reflect.KClass

data class UpdateUsdaApiKeyCommand(val apiKey: String? = null) : Command

internal class UpdateUsdaApiKeyCommandHandler(
    private val localFoodPreferences: LocalFoodPreferencesDataSource
) : CommandHandler<UpdateUsdaApiKeyCommand, Unit, Unit> {
    override val commandType: KClass<UpdateUsdaApiKeyCommand>
        get() = UpdateUsdaApiKeyCommand::class

    override suspend fun handle(command: UpdateUsdaApiKeyCommand): Result<Unit, Unit> {
        localFoodPreferences.updateUsdaApiKey(command.apiKey)
        return Ok(Unit)
    }
}
