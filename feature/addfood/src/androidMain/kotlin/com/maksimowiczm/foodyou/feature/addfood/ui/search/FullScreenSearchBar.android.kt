package com.maksimowiczm.foodyou.feature.addfood.ui.search

import android.os.Build
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.maksimowiczm.foodyou.core.ext.lambda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal actual fun FullScreenSearchBar(
    recentQueries: List<String>,
    onSearch: (String) -> Unit,
    state: SearchFoodScreenState,
    inputField: @Composable (() -> Unit)
) {
    // It's broken below API 28, so we don't show it
    if (Build.VERSION.SDK_INT < 28) {
        return
    }

    val coroutineScope = rememberCoroutineScope()

    ExpandedFullScreenSearchBar(
        state = state.searchBarState,
        inputField = inputField
    ) {
        ProductSearchBarSuggestions(
            recentQueries = recentQueries,
            onSearch = coroutineScope.lambda<String> {
                onSearch(it)
                state.textFieldState.setTextAndPlaceCursorAtEnd(it)
                state.searchBarState.animateToCollapsed()
            },
            onFill = {
                state.textFieldState.setTextAndPlaceCursorAtEnd(it)
            }
        )
    }
}
