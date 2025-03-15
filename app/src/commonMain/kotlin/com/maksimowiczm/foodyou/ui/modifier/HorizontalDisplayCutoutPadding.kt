package com.maksimowiczm.foodyou.ui.modifier

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.horizontalDisplayCutoutPadding(): Modifier {
    val insets = WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)

    return windowInsetsPadding(insets).consumeWindowInsets(insets)
}
