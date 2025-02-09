package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery

@Composable
fun rememberSearchBarState(
    initialExpanded: Boolean = false,
    initialTextFieldState: TextFieldState = rememberTextFieldState(),
    initialRecentQueries: List<ProductQuery> = emptyList()
): SearchBarState = rememberSaveable(
    initialExpanded,
    initialTextFieldState,
    initialRecentQueries,
    saver = Saver(
        save = { state ->
            state.expanded
        },
        restore = {
            SearchBarState(
                expanded = it,
                textFieldState = initialTextFieldState,
                recentQueries = initialRecentQueries
            )
        }
    )
) {
    SearchBarState(
        expanded = initialExpanded,
        textFieldState = initialTextFieldState,
        recentQueries = initialRecentQueries
    )
}

@Stable
class SearchBarState(
    expanded: Boolean,
    val textFieldState: TextFieldState,
    recentQueries: List<ProductQuery>
) {
    var expanded: Boolean by mutableStateOf(expanded)
        private set

    var recentQueries: List<ProductQuery> by mutableStateOf(recentQueries)
        private set

    fun updateRecentQueries(queries: List<ProductQuery>) {
        recentQueries = queries
    }

    fun requestExpandedState(state: Boolean) {
        expanded = state
    }
}
