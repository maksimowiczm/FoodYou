package com.maksimowiczm.foodyou.app.ui.food.recipe

import com.maksimowiczm.foodyou.food.domain.entity.FoodId

internal sealed interface CreateRecipeEvent {
    data class Created(val recipeId: FoodId.Recipe) : CreateRecipeEvent
}
