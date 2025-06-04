package com.maksimowiczm.foodyou.feature.barcodescanner

import androidx.compose.runtime.Composable

// Don't care about predictive back because multiplatform API is awful
@Composable
expect fun FullScreenCameraBarcodeScanner(onBarcodeScan: (String) -> Unit, onClose: () -> Unit)
