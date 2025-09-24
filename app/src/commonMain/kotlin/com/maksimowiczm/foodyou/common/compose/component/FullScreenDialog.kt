package com.maksimowiczm.foodyou.common.compose.component

import androidx.compose.runtime.*

@Composable
expect fun FullScreenDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit)
