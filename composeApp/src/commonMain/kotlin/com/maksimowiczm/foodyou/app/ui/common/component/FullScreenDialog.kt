package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.runtime.*

@Composable
expect fun FullScreenDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit)
