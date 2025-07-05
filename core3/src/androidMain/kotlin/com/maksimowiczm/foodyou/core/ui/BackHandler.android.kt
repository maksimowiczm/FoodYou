package com.maksimowiczm.foodyou.core.ui

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandlerImpl(enabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(
        enabled = enabled,
        onBack = onBack
    )
}
