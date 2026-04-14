package com.maksimowiczm.foodyou.device.application

import com.maksimowiczm.foodyou.analytics.domain.AppLaunchedEvent
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.device.domain.DeviceSettingsRepository
import com.maksimowiczm.foodyou.device.domain.RandomColorProvider
import com.maksimowiczm.foodyou.device.domain.randomizeTheme
import kotlinx.coroutines.flow.first

/** Handles theme randomization when the application is launched. */
class RandomizeThemeOnAppLaunchHandler(
    private val deviceSettingsRepository: DeviceSettingsRepository,
    private val colorProvider: RandomColorProvider,
) : EventHandler<AppLaunchedEvent> {
    override suspend fun handle(event: AppLaunchedEvent) {
        val settings = deviceSettingsRepository.observe().first()
        if (settings.themeSettings.randomizeOnLaunch) {
            deviceSettingsRepository.update { it.randomizeTheme(colorProvider) }
        }
    }
}
