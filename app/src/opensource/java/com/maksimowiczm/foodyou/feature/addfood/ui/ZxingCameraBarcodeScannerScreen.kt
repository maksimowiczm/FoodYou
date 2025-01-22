package com.maksimowiczm.foodyou.feature.addfood.ui

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.databinding.CameraBarcodeLayoutBinding
import com.maksimowiczm.foodyou.feature.addfood.ui.search.BarcodeScannerScreen

val zxingCameraBarcodeScannerScreen = BarcodeScannerScreen { onBarcodeScan, modifier ->
    var barcodeView by remember { mutableStateOf<CompoundBarcodeView?>(null) }

    val latestOnBarcodeScanned by rememberUpdatedState(onBarcodeScan)
    DisposableEffect(barcodeView) {
        barcodeView?.resume()

        val callback = BarcodeCallback { result ->
            result?.let { latestOnBarcodeScanned(it.text) }
        }

        barcodeView?.decodeSingle(callback)

        onDispose {
            barcodeView?.pause()
        }
    }

    Box(
        modifier = modifier.systemBarsPadding()
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
    }
}
