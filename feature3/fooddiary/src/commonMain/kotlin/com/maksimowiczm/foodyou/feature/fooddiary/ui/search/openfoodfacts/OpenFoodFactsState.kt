package com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts

internal sealed interface OpenFoodFactsState {
    data object PrivacyPolicyRequested : OpenFoodFactsState

    data object Loading : OpenFoodFactsState

    data class Loaded(val productsFound: Int) : OpenFoodFactsState

    data object Error : OpenFoodFactsState
}
