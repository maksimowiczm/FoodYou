package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.barcodescanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraBarcodeScannerScreen(
    onBarcodeScan: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
)
