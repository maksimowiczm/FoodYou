package com.maksimowiczm.foodyou.core.ui.modifier

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.horizontalSystemBarsPadding(): Modifier {
    val insets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)

    return windowInsetsPadding(insets).consumeWindowInsets(insets)
}
