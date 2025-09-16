package com.maksimowiczm.foodyou.app.ui.food.shared.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
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

@OptIn(ExperimentalMaterial3Api::class)
@Stable
internal class FoodSearchAppState(
    val searchBarState: SearchBarState,
    val searchTextFieldState: TextFieldState,
    showBarcodeScannerState: MutableState<Boolean>,
) {
    var showBarcodeScanner by showBarcodeScannerState
}
