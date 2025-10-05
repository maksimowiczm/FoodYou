package com.maksimowiczm.foodyou.device.domain

class Device(name: String, themeSettings: ThemeSettings) {
    var name: String = name
        private set

    var themeSettings = themeSettings
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
}
