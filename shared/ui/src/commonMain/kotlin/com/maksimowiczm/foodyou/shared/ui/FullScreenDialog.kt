package com.maksimowiczm.foodyou.shared.ui

import androidx.compose.runtime.Composable

@Composable
expect fun FullScreenDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit)
