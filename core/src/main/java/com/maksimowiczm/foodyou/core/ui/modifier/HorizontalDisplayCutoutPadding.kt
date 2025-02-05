package com.maksimowiczm.foodyou.core.ui.modifier

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.horizontalDisplayCutoutPadding() =
    this.padding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal).asPaddingValues())
