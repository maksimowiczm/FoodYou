package com.maksimowiczm.foodyou.core.util

expect class ClipboardManager {
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
