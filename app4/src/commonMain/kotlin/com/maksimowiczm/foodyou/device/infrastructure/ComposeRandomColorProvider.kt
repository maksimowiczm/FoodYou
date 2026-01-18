package com.maksimowiczm.foodyou.device.infrastructure

import com.maksimowiczm.foodyou.device.domain.RandomColorProvider

val composeRandomColorProvider
    get() = RandomColorProvider {
        androidx.compose.ui.graphics.Color((0xFF000000..0xFFFFFFFF).random()).value
    }
