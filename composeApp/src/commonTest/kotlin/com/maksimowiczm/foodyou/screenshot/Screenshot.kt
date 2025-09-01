package com.maksimowiczm.foodyou.screenshot

import androidx.compose.runtime.Composable

interface Screenshot {
    val name: String

    @Composable fun Content()
}
