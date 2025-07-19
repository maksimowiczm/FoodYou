package com.maksimowiczm.foodyou.feature.food.ui.recipe.create

import com.maksimowiczm.foodyou.feature.food.domain.FoodId

internal sealed interface CreateRecipeEvent {
    data class Created(val recipeId: FoodId.Recipe) : CreateRecipeEvent
}
