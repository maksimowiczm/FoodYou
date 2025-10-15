package com.maksimowiczm.foodyou.app.ui.food.search

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
    showBarcodeScanner: Boolean = false,
): FoodSearchAppState {
    val showBarcodeScanner =
        rememberSaveable(showBarcodeScanner) { mutableStateOf(showBarcodeScanner) }

    return remember(searchBarState, searchTextFieldState, showBarcodeScanner) {
        FoodSearchAppState(
            searchBarState = searchBarState,
            searchTextFieldState = searchTextFieldState,
            showBarcodeScannerState = showBarcodeScanner,
        )
    }
}

@Stable
internal class FoodSearchAppState(
    val searchBarState: SearchBarState,
    val searchTextFieldState: TextFieldState,
    showBarcodeScannerState: MutableState<Boolean>,
) {
    var showBarcodeScanner by showBarcodeScannerState
}
