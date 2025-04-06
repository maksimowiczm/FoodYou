package com.maksimowiczm.foodyou.core.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(
        enabled = enabled,
        onBack = onBack
    )
}
