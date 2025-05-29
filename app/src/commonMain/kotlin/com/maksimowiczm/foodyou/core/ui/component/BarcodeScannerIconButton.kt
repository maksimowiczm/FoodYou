package com.maksimowiczm.foodyou.core.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BarcodeScannerIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_barcode_scanner),
            contentDescription = stringResource(Res.string.action_scan_barcode)
        )
    }
}
