package com.maksimowiczm.foodyou.feature.barcodescanner

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.core.ui.component.FullScreenDialog

@Composable
actual fun FullScreenCameraBarcodeScanner(onBarcodeScan: (String) -> Unit, onClose: () -> Unit) {
    FullScreenDialog(
        onDismissRequest = onClose
    ) {
        CameraBarcodeScannerScreen(
            onBarcodeScan = onBarcodeScan,
            onClose = onClose
        )
    }
}
