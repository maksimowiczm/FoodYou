package com.maksimowiczm.foodyou.feature.food.recipe.presentation

import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId

internal sealed interface CreateRecipeEvent {
    data class Created(val recipeId: FoodId.Recipe) : CreateRecipeEvent
}
