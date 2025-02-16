package com.maksimowiczm.foodyou.core.ui.res

fun Float.formatClipZeros(
    format: String = "%.2f"
) = if (this % 1 == 0f) {
    this.toInt().toString()
} else {
    format.format(this).trimEnd('0')
}
