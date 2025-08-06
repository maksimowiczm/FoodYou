package com.maksimowiczm.foodyou.shared.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

interface ClipboardManager {
    /**
     * Copy the given text to the clipboard.
     *
     * @param label The label for the clipboard entry.
     * @param text The text to copy.
     */
    fun copy(label: String, text: String)

    /**
     * Paste text from the clipboard.
     *
     * @return The pasted text, or null if there is no text in the clipboard.
     */
    fun paste(): String?
}

private val defaultClipboardManager: ClipboardManager =
    object : ClipboardManager {
        override fun copy(label: String, text: String) = Unit

        override fun paste() = null
    }

val LocalClipboardManager = staticCompositionLocalOf { defaultClipboardManager }

@Composable
fun ClipboardManagerProvider(clipboardManager: ClipboardManager, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalClipboardManager provides clipboardManager) { content() }
}
