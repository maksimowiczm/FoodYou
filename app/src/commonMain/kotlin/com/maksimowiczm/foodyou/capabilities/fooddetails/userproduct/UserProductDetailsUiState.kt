package com.maksimowiczm.foodyou.capabilities.fooddetails.userproduct

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct

@Immutable
data class UserProductDetailsUiState(
    val product: UserProduct,
    val isFavorite: Boolean,
    val suggestions: List<Quantity>,
    val selectedQuantity: Quantity,
    val scaledNutritionFacts: NutritionFacts?,
    val quantityTypes: List<QuantityType>,
    val selectedQuantityType: QuantityType,
)
