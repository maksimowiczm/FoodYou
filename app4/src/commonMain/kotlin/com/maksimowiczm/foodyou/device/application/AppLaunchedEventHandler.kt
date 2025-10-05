package com.maksimowiczm.foodyou.device.application

import com.maksimowiczm.foodyou.analytics.domain.AppLaunchedEvent
import com.maksimowiczm.foodyou.common.domain.event.EventHandler
import com.maksimowiczm.foodyou.device.domain.ColorProvider
import com.maksimowiczm.foodyou.device.domain.DeviceRepository

class AppLaunchedEventHandler(
    private val deviceRepository: DeviceRepository,
    private val colorProvider: ColorProvider,
) : EventHandler<AppLaunchedEvent> {
    override suspend fun handle(event: AppLaunchedEvent) {
        val device = deviceRepository.load()

        if (device.themeSettings.randomizeOnLaunch) {
            device.randomizeTheme(colorProvider)
        }

        deviceRepository.save(device)
    }
}
