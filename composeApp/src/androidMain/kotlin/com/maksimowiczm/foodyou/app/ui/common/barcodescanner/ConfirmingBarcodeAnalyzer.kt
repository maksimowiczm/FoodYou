package com.maksimowiczm.foodyou.app.ui.common.barcodescanner

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

/**
 * Decorator around [BarcodeAnalyzer] that requires the same barcode to appear
 * [confirmationsRequired] consecutive times before firing [onBarcode], and applies a
 * [penaltyPerMiss] to the streak counter on each missed frame instead of resetting it outright.
 *
 * [onCertainty] is called on every frame with a 0..1 progress fraction so the UI can reflect
 * scanning confidence.
 */
internal class ConfirmingBarcodeAnalyzer(
    scanFraction: Float,
    private val confirmationsRequired: Int,
    private val penaltyPerMiss: Int,
    private val onBarcode: (String) -> Unit,
    private val onCertainty: (Float) -> Unit,
) : ImageAnalysis.Analyzer {
    private val isCompleted = false
    private var lastBarcode: String? = null
    private var consecutiveCount: Int = 0

    private val inner = BarcodeAnalyzer(onBarcode = ::onFrame, scanFraction = scanFraction)

    override fun analyze(imageProxy: ImageProxy) {
        if (isCompleted) {
            imageProxy.close()
            return
        }
        inner.analyze(imageProxy)
    }

    private fun onFrame(barcode: String?) {
        if (barcode != null) {
            if (barcode == lastBarcode) {
                consecutiveCount++
            } else {
                lastBarcode = barcode
                consecutiveCount = 1
            }
        } else {
            consecutiveCount = (consecutiveCount - penaltyPerMiss).coerceAtLeast(0)
            if (consecutiveCount == 0) lastBarcode = null
        }

        onCertainty((consecutiveCount / confirmationsRequired.toFloat()).coerceIn(0f, 1f))

        if (consecutiveCount >= confirmationsRequired && !isCompleted) {
            onBarcode(lastBarcode!!)
        }
    }
}
