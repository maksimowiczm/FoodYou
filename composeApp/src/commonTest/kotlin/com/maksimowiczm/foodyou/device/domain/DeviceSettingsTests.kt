package com.maksimowiczm.foodyou.device.domain

import kotlin.test.Test
import kotlin.test.assertNotSame

class DeviceSettingsTests {
    @Test
    fun randomizeTheme_updatesThemeSettings() {
        val settings =
            DeviceSettings(
                name = "Test Device",
                themeSettings =
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
                    ),
                nutrientsColors =
                    NutrientsColors(proteins = null, carbohydrates = null, fats = null),
                language = null,
                hideScreen = false,
            )

        val updatedDevice = settings.randomizeTheme(colorProvider = { 0UL })

        assertNotSame(
            settings.themeSettings.theme,
            updatedDevice.themeSettings.theme,
            "Theme should be updated after randomizing theme.",
        )
    }
}
