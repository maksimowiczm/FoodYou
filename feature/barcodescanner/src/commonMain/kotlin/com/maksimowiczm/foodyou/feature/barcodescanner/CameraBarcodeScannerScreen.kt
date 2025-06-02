package com.maksimowiczm.foodyou.feature.barcodescanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.component.FullScreenDialog

@Composable
expect fun CameraBarcodeScannerScreen(
    onBarcodeScan: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
)

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
