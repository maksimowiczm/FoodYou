package com.maksimowiczm.foodyou.capabilities.fooddetails.swissfoodcomposition

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionProduct

@Immutable
sealed interface SwissFoodCompositionDetailsUiState {
    @Immutable data object Loading : SwissFoodCompositionDetailsUiState

    @Immutable data object NotFound : SwissFoodCompositionDetailsUiState

    @Immutable
    data class Details(
        val food: SwissFoodCompositionProduct,
        val isFavorite: Boolean,
        val suggestions: List<Quantity>,
        val selectedQuantity: Quantity,
        val scaledNutritionFacts: NutritionFacts?,
        val quantityTypes: List<QuantityType>,
        val selectedQuantityType: QuantityType,
    ) : SwissFoodCompositionDetailsUiState
}
