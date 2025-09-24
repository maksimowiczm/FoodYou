package com.maksimowiczm.foodyou.common.compose.extension

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle

@Composable fun TextStyle.toDp() = with(LocalDensity.current) { lineHeight.toDp() }
