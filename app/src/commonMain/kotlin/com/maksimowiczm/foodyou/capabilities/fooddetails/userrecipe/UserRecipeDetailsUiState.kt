package com.maksimowiczm.foodyou.capabilities.fooddetails.userrecipe

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe

@Immutable
data class UserRecipeDetailsUiState(
    val recipe: UserRecipe,
    val isFavorite: Boolean,
    val suggestions: List<Quantity>,
    val selectedQuantity: Quantity,
    val scaledNutritionFacts: NutritionFacts?,
    val ingredientScalingFactor: Double,
    val quantityTypes: List<QuantityType>,
    val selectedQuantityType: QuantityType,
)
