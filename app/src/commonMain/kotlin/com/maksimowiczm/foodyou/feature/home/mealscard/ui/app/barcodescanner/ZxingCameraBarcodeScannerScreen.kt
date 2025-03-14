package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.barcodescanner

import android.view.View
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.databinding.CameraBarcodeLayoutBinding
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

val zxingCameraBarcodeScannerScreen =
    BarcodeScannerScreen { onBarcodeScan, modifier ->
        var barcodeView by remember { mutableStateOf<CompoundBarcodeView?>(null) }
        var torchOn by rememberSaveable { mutableStateOf(false) }

        val latestOnBarcodeScanned by rememberUpdatedState(onBarcodeScan)
        DisposableEffect(barcodeView) {
            barcodeView?.resume()

            val callback = BarcodeCallback { result ->
                result?.let { latestOnBarcodeScanned(it.text) }
            }

            barcodeView?.decodeSingle(callback)

            val torchListener = object : DecoratedBarcodeView.TorchListener {
                override fun onTorchOn() {
                    torchOn = true
                }

                override fun onTorchOff() {
                    torchOn = false
                }
            }

            barcodeView?.setTorchListener(torchListener)

            onDispose {
                barcodeView?.pause()
            }
        }

        Box(
            modifier = modifier.navigationBarsPadding()
        ) {
            AndroidView(
                factory = { context ->
                    View.inflate(context, R.layout.camera_barcode_layout, null)
                },
                update = { view ->
                    val binding = CameraBarcodeLayoutBinding.bind(view)
                    barcodeView = binding.barcodeView
                }
            )
            FlashlightButton(
                enabled = torchOn,
                onClick = {
                    if (torchOn) {
                        barcodeView?.setTorchOff()
                    } else {
                        barcodeView?.setTorchOn()
                    }
                },
                modifier = Modifier
                    .safeGesturesPadding()
                    .padding(bottom = 16.dp)
                    .align(Alignment.BottomEnd)
                    .zIndex(1f)
            )
        }
    }

@Composable
private fun FlashlightButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val background by animateColorAsState(
        if (enabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        }
    )
    val content by animateColorAsState(
        if (enabled) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    )

    FilledIconButton(
        modifier = modifier,
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = background,
            contentColor = content
        )
    ) {
        Icon(
            painter = painterResource(
                if (enabled) {
                    Res.drawable.ic_flash_on
                } else {
                    Res.drawable.ic_flash_off
                }
            ),
            contentDescription = if (enabled) {
                stringResource(Res.string.action_disable_camera_flash)
            } else {
                stringResource(Res.string.action_enable_camera_flash)
            }
        )
    }
}
