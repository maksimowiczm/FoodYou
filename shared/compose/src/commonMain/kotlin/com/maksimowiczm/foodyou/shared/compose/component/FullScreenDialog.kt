package com.maksimowiczm.foodyou.shared.compose.component

import androidx.compose.runtime.Composable

@Composable
expect fun FullScreenDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit)
