package com.maksimowiczm.foodyou.app.ui.common.utility

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.widget.Toast
import foodyou.app.generated.resources.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

actual class ClipboardManagerImpl(
    private val context: Context,
    private val copyMessageProvider: () -> String = {
        runBlocking { getString(Res.string.neutral_copied) }
    },
) : ClipboardManager {
    private val clipboard: android.content.ClipboardManager
        get() =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager

    actual override fun copy(label: String, text: String) {
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            Toast.makeText(context, copyMessageProvider(), Toast.LENGTH_SHORT).show()
        }
    }

    actual override fun paste(): String? =
        runCatching {
                val clip = clipboard.primaryClip

                return if (clip != null && clip.itemCount > 0) {
                    clip.getItemAt(0).text.toString()
                } else {
                    null
                }
            }
            .getOrNull()
}
