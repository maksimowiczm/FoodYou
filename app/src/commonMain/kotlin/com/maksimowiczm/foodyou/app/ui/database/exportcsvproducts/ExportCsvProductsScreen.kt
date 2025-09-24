package com.maksimowiczm.foodyou.app.ui.database.exportcsvproducts

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
expect fun ExportCsvProductsScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
)
