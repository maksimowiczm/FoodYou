package com.maksimowiczm.foodyou.core.ui.component

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(
        enabled = enabled,
        onBack = onBack
    )
}
