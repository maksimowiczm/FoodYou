package com.maksimowiczm.foodyou.device.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class DeviceTest {
    @Test
    fun randomizeTheme_updatesThemeSettings() {
        val initialThemeSettings =
            ThemeSettings(
                randomizeOnLaunch = false,
                themeOption = ThemeOption.System,
                theme =
                    Theme.Custom(
                        seedColor = ULong.MAX_VALUE,
                        style = ThemeStyle.Expressive,
                        contrast = ThemeContrast.Default,
                        isAmoled = false,
                    ),
            )
        val device = testDevice(themeSettings = initialThemeSettings)

        device.randomizeTheme(colorProvider = { 0UL })

        assertNotSame(
            initialThemeSettings.theme,
            device.themeSettings.theme,
            "Theme should be updated after randomizing theme.",
        )
    }

    @Test
    fun updateTheme_disableThemeRandomization() {
        val initialThemeSettings =
            ThemeSettings(
                randomizeOnLaunch = true,
                themeOption = ThemeOption.System,
                theme = Theme.Default,
            )
        val device = testDevice(themeSettings = initialThemeSettings)

        device.updateTheme(Theme.Default)

        assertEquals(
            false,
            device.themeSettings.randomizeOnLaunch,
            "Randomization should be disabled after theme update.",
        )
    }
}
