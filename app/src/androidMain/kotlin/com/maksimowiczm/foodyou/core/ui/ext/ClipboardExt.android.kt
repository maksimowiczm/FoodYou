package com.maksimowiczm.foodyou.core.ui.ext

import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import androidx.compose.ui.platform.Clipboard

actual fun Clipboard.paste(): String? {
    val native = this.nativeClipboard
    if (native.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) != true) {
        return null
    }

    val item = native.primaryClip?.getItemAt(0) ?: return null
    val text = item.text
    return text?.toString()
}
