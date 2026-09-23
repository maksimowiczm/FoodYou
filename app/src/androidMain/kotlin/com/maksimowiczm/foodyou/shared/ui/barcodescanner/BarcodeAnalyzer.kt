package com.maksimowiczm.foodyou.shared.ui.barcodescanner

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer

/** Decodes a single barcode from each camera frame and reports the result. */
internal class BarcodeAnalyzer(
    private val onBarcode: (String?) -> Unit,
    private val scanFraction: Float,
) : ImageAnalysis.Analyzer {
    private val reader =
        MultiFormatReader().apply {
            setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to BarcodeFormat.entries))
        }

    override fun analyze(imageProxy: ImageProxy) {
        val plane = imageProxy.planes[0]
        val buffer = plane.buffer
        val bytes = ByteArray(buffer.remaining()).also { buffer.get(it) }
        val width = imageProxy.width
        val height = imageProxy.height
        val rowStride = plane.rowStride
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        imageProxy.close()

        val (dataBytes, dataWidth, dataHeight) =
            when (rotationDegrees) {
                90 -> Triple(rotateCW(bytes, width, height, rowStride), height, width)
                180 -> Triple(rotate180(bytes, width, height, rowStride), width, height)
                270 -> Triple(rotateCCW(bytes, width, height, rowStride), height, width)
                else -> Triple(stripStride(bytes, width, height, rowStride), width, height)
            }

        val cropSize = (minOf(dataWidth, dataHeight) * scanFraction).toInt()
        val cropLeft = (dataWidth - cropSize) / 2
        val cropTop = (dataHeight - cropSize) / 2

        try {
            val source =
                PlanarYUVLuminanceSource(
                    dataBytes,
                    dataWidth,
                    dataHeight,
                    cropLeft,
                    cropTop,
                    cropSize,
                    cropSize,
                    false,
                )
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            onBarcode(reader.decodeWithState(bitmap).text)
        } catch (_: NotFoundException) {
            onBarcode(null)
        } finally {
            reader.reset()
        }
    }
}

/**
 * Copy pixels without row-stride padding into a tightly packed array of size [width] × [height].
 */
private fun stripStride(data: ByteArray, width: Int, height: Int, rowStride: Int): ByteArray {
    if (rowStride == width) return data
    val out = ByteArray(width * height)
    for (y in 0 until height) {
        data.copyInto(out, y * width, y * rowStride, y * rowStride + width)
    }
    return out
}

/** Rotate 90° clockwise, stripping row-stride padding. Output size: [height] × [width]. */
private fun rotateCW(data: ByteArray, width: Int, height: Int, rowStride: Int): ByteArray {
    val out = ByteArray(width * height)
    for (y in 0 until height) {
        for (x in 0 until width) {
            out[x * height + (height - 1 - y)] = data[y * rowStride + x]
        }
    }
    return out
}

/** Rotate 90° counter-clockwise, stripping row-stride padding. Output size: [height] × [width]. */
private fun rotateCCW(data: ByteArray, width: Int, height: Int, rowStride: Int): ByteArray {
    val out = ByteArray(width * height)
    for (y in 0 until height) {
        for (x in 0 until width) {
            out[(width - 1 - x) * height + y] = data[y * rowStride + x]
        }
    }
    return out
}

/** Rotate 180°, stripping row-stride padding. Output size: [width] × [height]. */
private fun rotate180(data: ByteArray, width: Int, height: Int, rowStride: Int): ByteArray {
    val out = ByteArray(width * height)
    for (y in 0 until height) {
        for (x in 0 until width) {
            out[(height - 1 - y) * width + (width - 1 - x)] = data[y * rowStride + x]
        }
    }
    return out
}
