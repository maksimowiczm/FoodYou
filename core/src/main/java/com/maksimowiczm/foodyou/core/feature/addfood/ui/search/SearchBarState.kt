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
    expanded: Boolean = false,
    textFieldState: TextFieldState = rememberTextFieldState(),
    recentQueries: List<ProductQuery> = emptyList()
) = rememberSaveable(
    expanded,
    textFieldState,
    recentQueries,
    saver = Saver(
        save = { state ->
            state.expanded
        },
        restore = {
            SearchBarState(
                expanded = it,
                textFieldState = textFieldState,
                recentQueries = recentQueries
            )
        }
    )
) {
    SearchBarState(
        expanded = expanded,
        textFieldState = textFieldState,
        recentQueries = recentQueries
    )
}

@Stable
class SearchBarState(
    expanded: Boolean,
    val textFieldState: TextFieldState,
    recentQueries: List<ProductQuery>
) {
    var expanded: Boolean by mutableStateOf(expanded)
    var recentQueries: List<ProductQuery> by mutableStateOf(recentQueries)
}
