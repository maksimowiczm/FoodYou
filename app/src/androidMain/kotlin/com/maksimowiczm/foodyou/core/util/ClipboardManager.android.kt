package com.maksimowiczm.foodyou.core.util

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.widget.Toast
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.neutral_copied
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

actual class ClipboardManager(private val context: Context) {
    private val clipboard: android.content.ClipboardManager
        get() = context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as android.content.ClipboardManager

    private val copyMessage by lazy {
        runBlocking {
            getString(Res.string.neutral_copied)
        }
    }

    actual fun copy(label: String, text: String) {
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            Toast.makeText(context, copyMessage, Toast.LENGTH_SHORT).show()
        }
    }

    actual fun paste(): String? {
        val clip = clipboard.primaryClip

        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).text.toString()
        } else {
            null
        }
    }
}
