package com.maksimowiczm.foodyou.device.domain

import com.maksimowiczm.foodyou.common.domain.Language

data class DeviceSettings(
    val name: String,
    val themeSettings: ThemeSettings,
    val nutrientsColors: NutrientsColors,
    val language: Language?,
    val hideScreen: Boolean,
)
