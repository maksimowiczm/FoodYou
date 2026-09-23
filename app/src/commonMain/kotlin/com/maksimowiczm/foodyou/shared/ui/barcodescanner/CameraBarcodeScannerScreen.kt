package com.maksimowiczm.foodyou.shared.ui.barcodescanner

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
expect fun CameraBarcodeScannerScreen(
    onBarcodeScan: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
)
