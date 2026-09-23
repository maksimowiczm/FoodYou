package com.maksimowiczm.foodyou.shared.ui.component

import androidx.compose.runtime.*

@Composable
expect fun FullScreenDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit)
