package com.maksimowiczm.foodyou.device.domain

import com.maksimowiczm.foodyou.common.domain.Language
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

private fun testDevice(
    name: String = "Test Device",
    themeSettings: ThemeSettings =
        ThemeSettings(
            randomizeOnLaunch = false,
            themeOption = ThemeOption.System,
            theme = Theme.Default,
        ),
    nutrientsColors: NutrientsColors =
        NutrientsColors(proteins = null, carbohydrates = null, fats = null),
    privacySettings: PrivacySettings = PrivacySettings(foodYouServicesAllowed = true),
    language: Language? = null,
    hideScreen: Boolean = false,
): Device =
    Device(
        name = name,
        themeSettings = themeSettings,
        nutrientsColors = nutrientsColors,
        privacySettings = privacySettings,
        language = language,
        hideScreen = hideScreen,
    )
