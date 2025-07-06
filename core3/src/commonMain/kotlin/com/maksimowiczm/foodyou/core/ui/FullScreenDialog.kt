package com.maksimowiczm.foodyou.core.ui

import androidx.compose.runtime.Composable

@Composable
expect fun FullScreenDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit)
