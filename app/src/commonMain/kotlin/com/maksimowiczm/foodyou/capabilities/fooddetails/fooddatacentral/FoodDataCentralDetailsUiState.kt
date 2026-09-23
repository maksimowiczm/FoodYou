package com.maksimowiczm.foodyou.capabilities.fooddetails.fooddatacentral

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct

@Immutable
sealed interface FoodDataCentralDetailsUiState {
    @Immutable data object Loading : FoodDataCentralDetailsUiState

    @Immutable data object NotFound : FoodDataCentralDetailsUiState

    @Immutable data class Error(val message: String?) : FoodDataCentralDetailsUiState

    @Immutable
    data class Details(
        val food: FoodDataCentralProduct,
        val isFavorite: Boolean,
        val isLoading: Boolean,
        val suggestions: List<Quantity>,
        val selectedQuantity: Quantity,
        val scaledNutritionFacts: NutritionFacts?,
        val quantityTypes: List<QuantityType>,
        val selectedQuantityType: QuantityType,
    ) : FoodDataCentralDetailsUiState
}
