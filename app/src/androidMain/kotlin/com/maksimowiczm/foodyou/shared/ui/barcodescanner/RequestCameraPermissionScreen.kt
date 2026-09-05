package com.maksimowiczm.foodyou.shared.ui.barcodescanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RequestCameraPermissionScreen(
    onRequest: () -> Unit,
    shouldShowRationale: Boolean,
    modifier: Modifier = Modifier,
) {
    val text =
        if (shouldShowRationale)
            stringResource(Res.string.neutral_barcode_scanner_camera_request_rationale)
        else stringResource(Res.string.neutral_barcode_scanner_camera_request)

    Scaffold(modifier) { paddingValues ->
        Surface(onRequest) {
            Column(
                modifier =
                    Modifier.fillMaxSize()
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.action_tap_to_allow_access),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}
