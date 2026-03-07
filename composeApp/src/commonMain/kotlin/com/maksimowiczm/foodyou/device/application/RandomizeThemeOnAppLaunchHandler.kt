package com.maksimowiczm.foodyou.device.application

import com.maksimowiczm.foodyou.analytics.domain.AppLaunchedEvent
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
import com.maksimowiczm.foodyou.device.domain.RandomColorProvider

/**
 * Handles theme randomization when the application is launched.
 *
 * This handler responds to app launch events by checking if theme randomization is enabled in
 * device settings. If enabled, it generates a new random theme using the color provider and
 * persists the updated device state.
 *
 * @property deviceRepository Repository for loading and saving device state
 * @property colorProvider Provider for generating random theme colors
 */
class RandomizeThemeOnAppLaunchHandler(
    private val deviceRepository: DeviceRepository,
    private val colorProvider: RandomColorProvider,
) : EventHandler<AppLaunchedEvent> {
    override suspend fun handle(event: AppLaunchedEvent) {
        val device = deviceRepository.load()

        if (device.themeSettings.randomizeOnLaunch) {
            device.randomizeTheme(colorProvider)
        }

        deviceRepository.save(device)
    }
}
