package com.maksimowiczm.foodyou.features.food.details.openfoodfacts

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct

@Immutable
internal sealed interface OpenFoodFactsDetailsUiState {
    @Immutable data object Loading : OpenFoodFactsDetailsUiState

    @Immutable data object NotFound : OpenFoodFactsDetailsUiState

    @Immutable data class Error(val message: String?) : OpenFoodFactsDetailsUiState

    @Immutable
    data class Details(
        val food: OpenFoodFactsProduct,
        val isFavorite: Boolean,
        val isLoading: Boolean,
        val suggestions: List<Quantity>,
        val selectedQuantity: Quantity,
        val scaledNutritionFacts: NutritionFacts?,
        val quantityTypes: List<QuantityType>,
        val selectedQuantityType: QuantityType,
    ) : OpenFoodFactsDetailsUiState
}
