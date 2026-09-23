package com.maksimowiczm.foodyou.shared.ui.barcodescanner

/**
 * Strategy that decides when a detected barcode is "confirmed" and reports scanning progress.
 *
 * Each camera frame calls [onFrame] with the raw detection result (`null` = no barcode found).
 * Implementations accumulate evidence across frames and return a [Result] indicating current
 * confidence and whether the barcode should be accepted.
 */
internal fun interface BarcodeConfirmationStrategy {

    /**
     * Process one frame result.
     *
     * @param barcode The barcode detected in this frame, or `null` if none was found.
     * @return A [Result] with the current [Result.certainty] (0..1) and an optional
     *   [Result.confirmedBarcode] when the strategy decides the barcode is confirmed.
     */
    fun onFrame(barcode: String?): Result

    data class Result(
        /** Scanning confidence in the range 0..1 to drive UI feedback. */
        val certainty: Float,
        /** Non-null once the strategy has confirmed a barcode value. */
        val confirmedBarcode: String? = null,
    )
}

/**
 * Sliding-window buffer strategy.
 *
 * Keeps the last [bufferSize] frame results. A barcode is confirmed once the same value appears at
 * least [confirmationsRequired] times within that window. Missed frames only evict old results as
 * the buffer rolls forward, so a few dropped frames between successful detections do not reset
 * progress — making this robust on devices that cannot detect a barcode on every frame.
 */
internal class BufferBarcodeConfirmationStrategy(
    private val confirmationsRequired: Int,
    private val bufferSize: Int = confirmationsRequired * 2,
) : BarcodeConfirmationStrategy {
    private val buffer = ArrayDeque<String?>(bufferSize)

    override fun onFrame(barcode: String?): BarcodeConfirmationStrategy.Result {
        if (buffer.size >= bufferSize) buffer.removeFirst()
        buffer.addLast(barcode)

        val best = buffer.filterNotNull().groupingBy { it }.eachCount().maxByOrNull { it.value }
        val hitCount = best?.value ?: 0
        val certainty = (hitCount / confirmationsRequired.toFloat()).coerceIn(0f, 1f)
        val confirmed = if (hitCount >= confirmationsRequired) best!!.key else null

        return BarcodeConfirmationStrategy.Result(
            certainty = certainty,
            confirmedBarcode = confirmed,
        )
    }
}

/**
 * Consecutive-hits strategy.
 *
 * Requires [confirmationsRequired] consecutive frames where the **same** barcode is detected. Any
 * missed frame (or a different barcode) resets the counter. This is strict but very fast on devices
 * with reliable frame-by-frame detection.
 */
internal class ConsecutiveBarcodeConfirmationStrategy(private val confirmationsRequired: Int) :
    BarcodeConfirmationStrategy {
    private var consecutiveCount = 0
    private var lastBarcode: String? = null

    override fun onFrame(barcode: String?): BarcodeConfirmationStrategy.Result {
        if (barcode != null && barcode == lastBarcode) {
            consecutiveCount++
        } else {
            consecutiveCount = if (barcode != null) 1 else 0
            lastBarcode = barcode
        }

        val certainty = (consecutiveCount / confirmationsRequired.toFloat()).coerceIn(0f, 1f)
        val confirmed = if (consecutiveCount >= confirmationsRequired) barcode else null

        return BarcodeConfirmationStrategy.Result(
            certainty = certainty,
            confirmedBarcode = confirmed,
        )
    }
}

/**
 * Combines any number of [strategies] running them all in parallel on every frame.
 * - Whichever strategy confirms a barcode first wins.
 * - The reported [BarcodeConfirmationStrategy.Result.certainty] is the **maximum** across all
 *   strategies so the UI progress indicator always reflects the furthest-ahead path.
 */
internal class CombinedBarcodeConfirmationStrategy(
    private vararg val strategies: BarcodeConfirmationStrategy
) : BarcodeConfirmationStrategy {
    override fun onFrame(barcode: String?): BarcodeConfirmationStrategy.Result {
        val results = strategies.map { it.onFrame(barcode) }

        val certainty = results.maxOf { it.certainty }
        val confirmed = results.firstNotNullOfOrNull { it.confirmedBarcode }

        return BarcodeConfirmationStrategy.Result(
            certainty = certainty,
            confirmedBarcode = confirmed,
        )
    }
}
