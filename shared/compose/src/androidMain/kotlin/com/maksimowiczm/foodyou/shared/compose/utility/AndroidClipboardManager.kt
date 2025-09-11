package com.maksimowiczm.foodyou.shared.compose.utility

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.widget.Toast

fun interface CopyMessageProvider {
    fun getCopyMessage(): String
}

class AndroidClipboardManager(
    private val context: Context,
    private val copyMessageProvider: CopyMessageProvider,
) : ClipboardManager {
    private val clipboard: android.content.ClipboardManager
        get() =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager

    private val copyMessage: String
        get() = copyMessageProvider.getCopyMessage()

    override fun copy(label: String, text: String) {
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            Toast.makeText(context, copyMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun paste(): String? =
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
