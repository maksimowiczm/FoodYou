package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSearchTopBarState(
    initialExpanded: Boolean = false,
    initialRecentQueries: List<ProductQuery> = emptyList()
): SearchTopBarState {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState(
        initialValue = if (initialExpanded) SearchBarValue.Expanded else SearchBarValue.Collapsed
    )

    return remember {
        SearchTopBarState(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            initialRecentQueries = initialRecentQueries
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class SearchTopBarState(
    val textFieldState: TextFieldState,
    val searchBarState: SearchBarState,
    initialRecentQueries: List<ProductQuery>
) {
    var recentQueries by mutableStateOf(initialRecentQueries)
}
