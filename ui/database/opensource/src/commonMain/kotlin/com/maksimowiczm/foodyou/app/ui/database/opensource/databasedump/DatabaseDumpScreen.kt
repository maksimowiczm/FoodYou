package com.maksimowiczm.foodyou.app.ui.database.opensource.databasedump

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun DatabaseDumpScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
)
