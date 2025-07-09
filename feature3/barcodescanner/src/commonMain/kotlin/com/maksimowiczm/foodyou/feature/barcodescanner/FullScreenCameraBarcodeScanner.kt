package com.maksimowiczm.foodyou.feature.barcodescanner

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.core.ui.FullScreenDialog

// Don't care about predictive back because multiplatform API is awful
@Composable
fun FullScreenCameraBarcodeScanner(onBarcodeScan: (String) -> Unit, onClose: () -> Unit) {
    FullScreenDialog(
        onDismissRequest = onClose
    ) {
        CameraBarcodeScannerScreen(
            onBarcodeScan = onBarcodeScan,
            onClose = onClose
        )
    }
}

@Composable
fun FullScreenCameraBarcodeScanner(
    visible: Boolean,
    onBarcodeScan: (String) -> Unit,
    onClose: () -> Unit
) {
    if (visible) {
        FullScreenCameraBarcodeScanner(onBarcodeScan, onClose)
    }
}
