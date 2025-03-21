package com.maksimowiczm.foodyou.feature.diary.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A composable that will be displayed under the search bar in the search screen.
 *
 * @see SearchHome
 */
fun interface SearchHint {
    @Composable
    operator fun invoke(modifier: Modifier)
}
