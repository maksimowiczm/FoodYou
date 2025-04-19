package com.maksimowiczm.foodyou.core.ui.ext

import androidx.compose.ui.platform.Clipboard

/**
 * Clipboard extension to paste text from the clipboard.
 */
expect fun Clipboard.paste(): String?
