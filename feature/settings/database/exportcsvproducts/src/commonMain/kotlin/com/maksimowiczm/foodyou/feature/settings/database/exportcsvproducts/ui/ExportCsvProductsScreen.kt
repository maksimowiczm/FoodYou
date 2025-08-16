package com.maksimowiczm.foodyou.feature.settings.database.exportcsvproducts.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ExportCsvProductsScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
)
