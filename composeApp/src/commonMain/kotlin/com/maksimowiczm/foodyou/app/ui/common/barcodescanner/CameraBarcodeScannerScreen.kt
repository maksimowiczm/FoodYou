package com.maksimowiczm.foodyou.app.ui.common.barcodescanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraBarcodeScannerScreen(
    onBarcodeScan: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
)
