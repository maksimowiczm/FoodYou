package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextRange
import com.maksimowiczm.foodyou.data.model.ProductQuery

/**
 * Creates a [SearchTopBarState] that can be used to manage the state of a search top bar.
 *
 * The state will be recreated whenever the [query] or [recentQueries] change.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSearchTopBarState(
    initialExpanded: Boolean = false,
    query: String? = null,
    recentQueries: List<ProductQuery> = emptyList()
): SearchTopBarState {
    val query = query ?: ""

    val searchBarState = rememberSearchBarState(
        initialValue = if (initialExpanded) SearchBarValue.Expanded else SearchBarValue.Collapsed
    )

    val textFieldState = rememberTextFieldState(
        initialText = query,
        initialSelection = TextRange(query.length)
    )

    return remember(recentQueries) {
        SearchTopBarState(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            recentQueries = recentQueries
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class SearchTopBarState(
    val textFieldState: TextFieldState,
    val searchBarState: SearchBarState,
    val recentQueries: List<ProductQuery>
)
