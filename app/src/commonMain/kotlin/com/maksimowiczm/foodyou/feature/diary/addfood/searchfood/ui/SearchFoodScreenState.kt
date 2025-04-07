package com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
internal class SearchFoodScreenState(
    val textFieldState: TextFieldState,
    val searchBarState: SearchBarState,
    val lazyListState: LazyListState
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun rememberSearchFoodScreenState(): SearchFoodScreenState {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState(
        initialValue = SearchBarValue.Collapsed
    )
    val lazyListState = rememberLazyListState()

    return remember(textFieldState, searchBarState, lazyListState) {
        SearchFoodScreenState(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            lazyListState = lazyListState
        )
    }
}
