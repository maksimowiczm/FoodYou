package com.maksimowiczm.foodyou.ext

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag

fun SemanticsNodeInteractionsProvider.onNodeWithTag(tag: Any) = onNodeWithTag(tag.toString())
