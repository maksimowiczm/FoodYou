package com.maksimowiczm.foodyou.core.component

import androidx.compose.runtime.Composable

@Composable
expect fun FullScreenDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit)
