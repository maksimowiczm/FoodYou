package com.maksimowiczm.foodyou.shared.ui.extension

import androidx.compose.ui.platform.Clipboard

expect suspend fun Clipboard.copy(label: String, text: String)
