package com.maksimowiczm.foodyou.shared.ui

import androidx.compose.runtime.Composable

@Composable
fun BackHandler(enabled: Boolean = true, onBack: () -> Unit) {
    BackHandlerImpl(enabled, onBack)
}

@Composable internal expect fun BackHandlerImpl(enabled: Boolean, onBack: () -> Unit)
