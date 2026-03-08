package com.maksimowiczm.foodyou.app.ui.common.barcodescanner

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.ui.common.component.FullScreenDialog

@Composable
fun FullScreenCameraBarcodeScanner(onBarcodeScan: (String) -> Unit, onClose: () -> Unit) {
    // TODO
    //  Predictive back handling
    FullScreenDialog(onDismissRequest = onClose) {
        CameraBarcodeScannerScreen(onBarcodeScan = onBarcodeScan, onClose = onClose)
    }
}

@Composable
fun FullScreenCameraBarcodeScanner(
    visible: Boolean,
    onBarcodeScan: (String) -> Unit,
    onClose: () -> Unit,
) {
    if (visible) {
        FullScreenCameraBarcodeScanner(onBarcodeScan, onClose)
    }
}
