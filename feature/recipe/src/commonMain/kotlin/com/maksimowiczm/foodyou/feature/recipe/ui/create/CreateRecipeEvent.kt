package com.maksimowiczm.foodyou.feature.recipe.ui.create

import com.maksimowiczm.foodyou.core.model.FoodId

internal sealed interface CreateRecipeEvent {
    data class RecipeCreated(val id: FoodId.Recipe) : CreateRecipeEvent
}
