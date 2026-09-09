package com.maksimowiczm.foodyou.features.diary

import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType

sealed interface UpdateAnonymousFoodDiaryEntryUiState {
    data object Loading : UpdateAnonymousFoodDiaryEntryUiState

    data class Loaded(
        val snapshot: FoodSnapshot,
        val suggestions: List<Quantity>,
        val selectedQuantity: Quantity,
        val scaledNutritionFacts: NutritionFacts?,
        val quantityTypes: List<QuantityType>,
        val selectedQuantityType: QuantityType,
        val ingredientScalingFactor: Double,
        val isTracked: Boolean,
        val relinkedSnapshot: MeasuredFoodSnapshot,
    ) : UpdateAnonymousFoodDiaryEntryUiState
}
