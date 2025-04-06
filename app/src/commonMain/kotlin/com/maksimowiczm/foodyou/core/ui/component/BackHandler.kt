package com.maksimowiczm.foodyou.core.ui.component

import androidx.compose.runtime.Composable

@Composable
expect fun BackHandler(enabled: Boolean, onBack: () -> Unit)
