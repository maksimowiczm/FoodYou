package com.maksimowiczm.foodyou.ui.component

import androidx.compose.runtime.Composable

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)
