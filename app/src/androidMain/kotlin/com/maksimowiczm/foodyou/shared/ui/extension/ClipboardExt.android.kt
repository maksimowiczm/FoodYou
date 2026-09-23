package com.maksimowiczm.foodyou.shared.ui.extension

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

actual suspend fun Clipboard.copy(label: String, text: String) {
    // TODO
    //  for android <= 32, we should show toast, but since almost nobody use this versions of
    //  android, we can skip it for convenience
    setClipEntry(ClipEntry(ClipData.newPlainText(label, text)))
}
