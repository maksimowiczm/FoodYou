package com.maksimowiczm.foodyou.feature.food.ui.search

internal sealed interface OpenFoodFactsState {
    data object PrivacyPolicyRequested : OpenFoodFactsState

    data object Loading : OpenFoodFactsState

    data class Loaded(val productsFound: Int) : OpenFoodFactsState

    data object Error : OpenFoodFactsState
}
