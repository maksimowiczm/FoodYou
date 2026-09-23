package com.maksimowiczm.foodyou.shared.ui.utility

import kotlin.math.abs
import kotlin.math.round

/**
 * Formats this [Float] as a decimal string, rounding to [decimals] places and stripping trailing
 * zeros (and the decimal point itself if the result is a whole number).
 *
 * Delegates to [Double.formatCompact] after converting to [Double].
 *
 * @param decimals the maximum number of digits to keep after the decimal point. Must be
 *   non-negative. Defaults to `2`.
 * @return the formatted string, e.g. `1000.0f -> "1000"`, `3.10f -> "3.1"`.
 * @see Double.formatCompact
 */
fun Float.formatCompact(decimals: Int = 2): String = this.toDouble().formatCompact(decimals)

/**
 * Formats this [Double] as a decimal string, rounding to [decimals] places and stripping trailing
 * zeros.
 *
 * This function:
 * - Removes trailing zeros in the fractional part (`1.50 -> "1.5"`).
 * - Removes the decimal point entirely when the value rounds to a whole number (`1000.000 ->
 *   "1000"`, not `"1000.0"` or `"1000.00"`).
 * - Handles rounding carry correctly, e.g. `1999.999` with `decimals = 2` produces `"2000"` rather
 *   than an incorrect `"1999.100"`.
 *
 * Special values [Double.NaN], [Double.POSITIVE_INFINITY], and [Double.NEGATIVE_INFINITY] are
 * returned via their default [toString] representation rather than being formatted.
 *
 * ### Examples
 *
 * ```
 * 1000.000.formatClipZeros()   // "1000"
 * 1234.5678.formatClipZeros()  // "1234.57"
 * 1999.999.formatClipZeros()   // "2000"
 * (-3.10).formatClipZeros()    // "-3.1"
 * ```
 *
 * @param decimals the maximum number of digits to keep after the decimal point. Must be
 *   non-negative. Defaults to `2`.
 * @return the formatted string, with trailing zeros and, if applicable, the trailing decimal point
 *   removed.
 */
fun Double.formatCompact(decimals: Int = 2): String {
    require(decimals >= 0) { "decimals must be non-negative, was $decimals" }

    if (this.isNaN() || this.isInfinite()) return this.toString()

    var factor = 1.0
    repeat(decimals) { factor *= 10.0 }

    val rounded = round(this * factor) / factor
    val isNegative = rounded < 0
    val absValue = abs(rounded)

    val intPart = absValue.toLong()
    var fracPart = round((absValue - intPart) * factor).toLong()

    // handle carry, e.g. 1999.999 -> 2000.00 not 1999.100
    var finalInt = intPart
    if (fracPart >= factor.toLong()) {
        fracPart = 0
        finalInt += 1
    }

    if (fracPart == 0L) {
        return if (isNegative && finalInt != 0L) "-$finalInt" else finalInt.toString()
    }

    val fracStr = fracPart.toString().padStart(decimals, '0').trimEnd('0')

    return buildString {
        if (isNegative) append('-')
        append(finalInt)
        append('.')
        append(fracStr)
    }
}
