package com.maksimowiczm.foodyou.app.ui.database.opensource.exportcsvproducts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ExportCsvProductsScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
)
