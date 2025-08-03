package com.maksimowiczm.foodyou.core.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle

@Composable
fun TextStyle.toDp() = with(LocalDensity.current) { lineHeight.toDp() }
