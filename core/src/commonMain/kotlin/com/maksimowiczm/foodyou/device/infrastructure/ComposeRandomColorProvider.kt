package com.maksimowiczm.foodyou.device.infrastructure

import com.maksimowiczm.foodyou.device.domain.RandomColorProvider

internal class ComposeRandomColorProvider : RandomColorProvider {
    override fun random(alpha: Int): ULong = ((0xFF000000..0xFFFFFFFF).random() shl 32).toULong()
}
