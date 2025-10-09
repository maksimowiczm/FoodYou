package com.maksimowiczm.foodyou.device.infrastructure

import com.maksimowiczm.foodyou.device.domain.ColorProvider

val composeColorProvider
    get() = ColorProvider {
        androidx.compose.ui.graphics.Color((0xFF000000..0xFFFFFFFF).random()).value
    }
