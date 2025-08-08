package com.maksimowiczm.foodyou.shared.barcodescanner

import android.view.View
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.google.zxing.client.android.R as zxingR
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.maksimowiczm.foodyou.shared.barcodescanner.databinding.CameraBarcodeLayoutBinding
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun ZxingCameraBarcodeScannerScreen(
    onBarcodeScan: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var barcodeView by remember { mutableStateOf<CompoundBarcodeView?>(null) }
    var torchOn by rememberSaveable { mutableStateOf(false) }

    val latestOnBarcodeScanned by rememberUpdatedState(onBarcodeScan)
    DisposableEffect(barcodeView) {
        barcodeView?.resume()

        val callback = BarcodeCallback { result -> result?.let { latestOnBarcodeScanned(it.text) } }

        barcodeView?.decodeSingle(callback)

        val torchListener =
            object : DecoratedBarcodeView.TorchListener {
                override fun onTorchOn() {
                    torchOn = true
                }

                override fun onTorchOff() {
                    torchOn = false
                }
            }

        barcodeView?.setTorchListener(torchListener)

        barcodeView?.setStatusText("")

        onDispose { barcodeView?.pause() }
    }

    Box(modifier) {
        AndroidView(
            factory = { context -> View.inflate(context, R.layout.camera_barcode_layout, null) },
            update = { view ->
                val binding = CameraBarcodeLayoutBinding.bind(view)
                barcodeView = binding.barcodeView
            },
        )

        Column(
            modifier = Modifier.safeGesturesPadding().align(Alignment.BottomCenter).zIndex(1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FlashlightButton(
                enabled = torchOn,
                onClick = {
                    if (torchOn) {
                        barcodeView?.setTorchOff()
                    } else {
                        barcodeView?.setTorchOn()
                    }
                },
            )
            Surface(
                color = MaterialTheme.colorScheme.scrim,
                modifier = Modifier.navigationBarsPadding().alpha(.5f),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    text = androidStringResource(zxingR.string.zxing_msg_default_status),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun FlashlightButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val background by
        animateColorAsState(
            if (enabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    val content by
        animateColorAsState(
            if (enabled) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

    FilledIconButton(
        modifier = modifier,
        onClick = onClick,
        colors =
            IconButtonDefaults.filledIconButtonColors(
                containerColor = background,
                contentColor = content,
            ),
    ) {
        Icon(
            imageVector =
                if (enabled) {
                    Icons.Default.FlashOn
                } else {
                    Icons.Default.FlashOff
                },
            contentDescription =
                if (enabled) {
                    stringResource(Res.string.action_disable_camera_flash)
                } else {
                    stringResource(Res.string.action_enable_camera_flash)
                },
        )
    }
}
