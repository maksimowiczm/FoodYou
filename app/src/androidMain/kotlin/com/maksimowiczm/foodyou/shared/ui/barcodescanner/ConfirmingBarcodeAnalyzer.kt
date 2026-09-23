package com.maksimowiczm.foodyou.shared.ui.barcodescanner

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

/**
 * Decorator around [BarcodeAnalyzer] that confirms a barcode by delegating to an injectable
 * [BarcodeConfirmationStrategy].
 *
 * [onCertainty] is called on every frame with a 0..1 progress fraction so the UI can reflect
 * scanning confidence.
 *
 * @param strategy Determines when a barcode is confirmed and reports per-frame certainty. Use
 *   [BufferBarcodeConfirmationStrategy] (default) for devices that miss frames, or
 *   [ConsecutiveBarcodeConfirmationStrategy] for strict consecutive-hit confirmation.
 */
internal class ConfirmingBarcodeAnalyzer(
    scanFraction: Float,
    private val strategy: BarcodeConfirmationStrategy,
    private val onBarcode: (String) -> Unit,
    private val onCertainty: (Float) -> Unit,
) : ImageAnalysis.Analyzer {
    private var isCompleted = false

    private val inner = BarcodeAnalyzer(onBarcode = ::onFrame, scanFraction = scanFraction)

    override fun analyze(imageProxy: ImageProxy) {
        if (isCompleted) {
            imageProxy.close()
            return
        }
        inner.analyze(imageProxy)
    }

    private fun onFrame(barcode: String?) {
        val result = strategy.onFrame(barcode)
        onCertainty(result.certainty)

        if (result.confirmedBarcode != null && !isCompleted) {
            isCompleted = true
            onBarcode(result.confirmedBarcode)
        }
    }
}
