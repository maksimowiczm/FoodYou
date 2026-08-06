package com.maksimowiczm.foodyou.features.food.details.userrecipe

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe

@Immutable
internal data class UserRecipeDetailsUiState(
    val recipe: UserRecipe? = null,
    val isFavorite: Boolean = false,
    val suggestions: List<Quantity> = emptyList(),
    val selectedQuantity: Quantity? = null,
    val scaledNutritionFacts: NutritionFacts? = null,
    val ingredientScalingFactor: Double = 1.0,
    val quantityTypes: List<QuantityType> = emptyList(),
    val selectedQuantityType: QuantityType? = null,
)
