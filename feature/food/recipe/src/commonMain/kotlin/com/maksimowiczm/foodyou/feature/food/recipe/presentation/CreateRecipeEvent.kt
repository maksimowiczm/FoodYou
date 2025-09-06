package com.maksimowiczm.foodyou.feature.food.recipe.presentation

import com.maksimowiczm.foodyou.food.domain.entity.FoodId

internal sealed interface CreateRecipeEvent {
    data class Created(val recipeId: FoodId.Recipe) : CreateRecipeEvent
}
