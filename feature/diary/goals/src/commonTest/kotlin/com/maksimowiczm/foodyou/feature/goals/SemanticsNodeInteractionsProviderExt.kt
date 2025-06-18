package com.maksimowiczm.foodyou.feature.goals

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag

fun SemanticsNodeInteractionsProvider.onNodeWithTag(tag: Any) = onNodeWithTag(tag.toString())
