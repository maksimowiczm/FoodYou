package com.maksimowiczm.foodyou.ui.res

import java.util.Locale

fun Float.formatClipZeros(format: String = "%.3f", locale: Locale = Locale.ENGLISH) =
    if (this % 1 == 0f) {
        this.toInt().toString()
    } else {
        format.format(locale, this).trimEnd('0').trimEnd { !it.isDigit() }
    }
