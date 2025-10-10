package com.maksimowiczm.foodyou.device.domain

class Device(name: String, themeSettings: ThemeSettings, privacySettings: PrivacySettings) {
    var name: String = name
        private set

    var themeSettings: ThemeSettings = themeSettings
        private set

    var privacySettings: PrivacySettings = privacySettings
        private set

    fun randomizeTheme(colorProvider: ColorProvider) {
        val isAmoled =
            when (val theme = themeSettings.theme) {
                is Theme.Custom -> theme.isAmoled
                else -> false
            }

        val newTheme = ThemeSettings.random(colorProvider = colorProvider, isAmoled = isAmoled)

        themeSettings = themeSettings.copy(theme = newTheme)
    }

    fun updatePrivacySettings(transform: (PrivacySettings) -> PrivacySettings) {
        privacySettings = transform(privacySettings)
    }
}
