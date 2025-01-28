package com.maksimowiczm.foodyou.feature.addfood.ui.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

fun interface BarcodeScannerScreen {
    @Composable
    operator fun invoke(
        onBarcodeScan: (String) -> Unit,
        modifier: Modifier
    )
}
