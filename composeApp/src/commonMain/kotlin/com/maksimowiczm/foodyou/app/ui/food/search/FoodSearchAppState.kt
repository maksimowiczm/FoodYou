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
internal fun rememberFoodSearchAppState(
    searchBarState: SearchBarState = rememberSearchBarState(),
    searchTextFieldState: TextFieldState = rememberTextFieldState(),
    foodSource: FoodSource = FoodSource.Favorite,
    showBarcodeScanner: Boolean = false,
): FoodSearchAppState {
    val showBarcodeScanner =
        rememberSaveable(showBarcodeScanner) { mutableStateOf(showBarcodeScanner) }

    val foodSource = rememberSaveable(foodSource) { mutableStateOf(foodSource) }

    val listStates = rememberListStates()

    return remember(
        searchBarState,
        searchTextFieldState,
        foodSource,
        showBarcodeScanner,
        listStates,
    ) {
        FoodSearchAppState(
            searchBarState = searchBarState,
            searchTextFieldState = searchTextFieldState,
            foodSourceState = foodSource,
            showBarcodeScannerState = showBarcodeScanner,
            listStates = listStates,
        )
    }
}

@Stable
internal class FoodSearchAppState(
    val searchBarState: SearchBarState,
    val searchTextFieldState: TextFieldState,
    foodSourceState: MutableState<FoodSource>,
    showBarcodeScannerState: MutableState<Boolean>,
    val listStates: ListStates,
) {
    var foodSource by foodSourceState
    var showBarcodeScanner by showBarcodeScannerState
}

@Immutable
internal class ListStates(
    val favorite: LazyListState,
    val yourFood: LazyListState,
    val openFoodFacts: LazyListState,
    val usda: LazyListState,
)

@Composable
private fun rememberListStates(): ListStates {
    val favorite = rememberLazyListState()
    val yourFood = rememberLazyListState()
    val openFoodFacts = rememberLazyListState()
    val usda = rememberLazyListState()

    return remember(favorite, yourFood, openFoodFacts, usda) {
        ListStates(
            favorite = favorite,
            yourFood = yourFood,
            openFoodFacts = openFoodFacts,
            usda = usda,
        )
    }
}
