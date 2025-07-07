package com.maksimowiczm.foodyou.core.ui.res

import java.util.Locale

actual fun Float.formatClipZeros(format: String) = if (this % 1 == 0f) {
    toInt().toString()
} else {
    format.format(Locale.ENGLISH, this).trimEnd('0').trimEnd { !it.isDigit() }.ifEmpty { "0" }
}
