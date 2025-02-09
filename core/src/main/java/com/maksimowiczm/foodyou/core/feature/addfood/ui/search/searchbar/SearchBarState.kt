package com.maksimowiczm.foodyou.core.feature.addfood.ui.search.searchbar

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery

@Stable
interface SearchBarState {
    val expanded: Boolean
    fun requestExpandedState(state: Boolean)

    val textFieldState: TextFieldState

    val recentQueries: List<ProductQuery>
    fun updateRecentQueries(queries: List<ProductQuery>)
}
