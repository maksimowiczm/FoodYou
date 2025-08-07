package com.maksimowiczm.foodyou.feature.shared.usecase

import com.maksimowiczm.foodyou.business.settings.application.command.UpdateSettingsCommand
import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus

fun interface UpdateSettingsUseCase {
    suspend fun update(settings: Settings)
}

internal class UpdateSettingsUseCaseImpl(private val commandBus: CommandBus) :
    UpdateSettingsUseCase {
    override suspend fun update(settings: Settings) {
        commandBus.dispatch<Unit, Unit>(UpdateSettingsCommand(settings))
    }
}
