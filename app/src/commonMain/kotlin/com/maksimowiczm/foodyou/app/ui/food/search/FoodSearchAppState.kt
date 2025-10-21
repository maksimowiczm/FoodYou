package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun rememberFoodSearchAppState(
    searchBarState: SearchBarState = rememberSearchBarState(),
    searchTextFieldState: TextFieldState = rememberTextFieldState(),
    showBarcodeScanner: Boolean = false,
): FoodSearchAppState {
    val showBarcodeScanner =
        rememberSaveable(showBarcodeScanner) { mutableStateOf(showBarcodeScanner) }

    val listStates = rememberListStates()

    return remember(searchBarState, searchTextFieldState, showBarcodeScanner, listStates) {
        FoodSearchAppState(
            searchBarState = searchBarState,
            searchTextFieldState = searchTextFieldState,
            showBarcodeScannerState = showBarcodeScanner,
            listStates = listStates,
        )
    }
}

@Stable
class FoodSearchAppState(
    val searchBarState: SearchBarState,
    val searchTextFieldState: TextFieldState,
    showBarcodeScannerState: MutableState<Boolean>,
    val listStates: ListStates,
) {
    var showBarcodeScanner by showBarcodeScannerState
}

class ListStates(
    val recent: LazyListState,
    val yourFood: LazyListState,
    val openFoodFacts: LazyListState,
    val usda: LazyListState,
    val swiss: LazyListState,
)

@Composable
private fun rememberListStates(): ListStates {
    val recent = rememberLazyListState()
    val yourFood = rememberLazyListState()
    val openFoodFacts = rememberLazyListState()
    val usda = rememberLazyListState()
    val swiss = rememberLazyListState()

    return remember(recent, yourFood, openFoodFacts, usda, swiss) {
        ListStates(
            recent = recent,
            yourFood = yourFood,
            openFoodFacts = openFoodFacts,
            usda = usda,
            swiss = swiss,
        )
    }
}
