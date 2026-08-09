package com.maksimowiczm.foodyou.capabilities.fooddetails.userproduct

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct

@Immutable
data class UserProductDetailsUiState(
    val product: UserProduct? = null,
    val isFavorite: Boolean = false,
    val suggestions: List<Quantity> = emptyList(),
    val selectedQuantity: Quantity? = null,
    val scaledNutritionFacts: NutritionFacts? = null,
    val quantityTypes: List<QuantityType> = emptyList(),
    val selectedQuantityType: QuantityType? = null,
)
