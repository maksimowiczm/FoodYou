package com.maksimowiczm.foodyou.app.ui.database.databasedump

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
expect fun DatabaseDumpScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
)
