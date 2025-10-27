package com.maksimowiczm.foodyou.device.domain

import kotlin.test.Test
import kotlin.test.assertNotSame

class DeviceTest {
    @Test
    fun randomizeTheme_updatesThemeSettings() {
        val device = testDevice()
        val initialThemeSettings = device.themeSettings

        device.randomizeTheme(colorProvider = { 0xFF0000UL })

        assertNotSame(
            initialThemeSettings,
            device.themeSettings,
            "Theme settings should be updated after randomizing theme.",
        )
    }
}
