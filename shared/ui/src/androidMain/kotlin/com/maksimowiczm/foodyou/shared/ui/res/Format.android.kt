package com.maksimowiczm.foodyou.shared.ui.res

import java.util.Locale

actual fun Float.formatClipZeros(format: String) =
    if (this % 1 == 0f) {
        toInt().toString()
    } else {
        // 1000.000 -> 1000
        // 1000 -> 1000
        val text = format.format(Locale.ENGLISH, this)

        if (text.contains('.')) {
            // Remove trailing zeros and dot if necessary
            text.replace(Regex("\\.?0+$"), "")
        } else {
            text
        }
    }

actual fun Double.formatClipZeros(format: String) =
    if (this % 1 == 0.0) {
        toInt().toString()
    } else {
        // 1000.000 -> 1000
        // 1000 -> 1000
        val text = format.format(Locale.ENGLISH, this)

        if (text.contains('.')) {
            // Remove trailing zeros and dot if necessary
            text.replace(Regex("\\.?0+$"), "")
        } else {
            text
        }
    }
