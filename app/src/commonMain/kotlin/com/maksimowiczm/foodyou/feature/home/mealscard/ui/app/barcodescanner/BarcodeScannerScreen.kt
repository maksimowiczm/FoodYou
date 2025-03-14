package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.barcodescanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform specific implementation of the barcode scanner screen.
 */
fun interface BarcodeScannerScreen {
    @Composable
    operator fun invoke(onBarcodeScan: (String) -> Unit, modifier: Modifier)
}
