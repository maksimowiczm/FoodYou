package com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.compose

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class AddFoodSearchState(
    val textFieldState: TextFieldState,
    val searchBarState: SearchBarState,
    val lazyListState: LazyListState
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAddFoodSearchState(): AddFoodSearchState {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState(
        initialValue = SearchBarValue.Collapsed
    )
    val lazyListState = rememberLazyListState()

    return remember(
        textFieldState,
        searchBarState,
        lazyListState
    ) {
        AddFoodSearchState(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            lazyListState = lazyListState
        )
    }
}
