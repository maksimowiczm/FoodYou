package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery

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
    val searchBarState = rememberSearchBarState(
        initialValue = if (initialExpanded) SearchBarValue.Expanded else SearchBarValue.Collapsed
    )

    return remember(
        query,
        recentQueries
    ) {
        SearchTopBarState(
            query = query ?: "",
            searchBarState = searchBarState,
            recentQueries = recentQueries
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class SearchTopBarState(
    val query: String,
    val searchBarState: SearchBarState,
    val recentQueries: List<ProductQuery>
)
