package com.maksimowiczm.foodyou.feature.recipe.ui.create

import com.maksimowiczm.foodyou.core.domain.model.FoodId

sealed interface CreateRecipeEvent {
    data class RecipeCreated(val id: FoodId.Recipe) : CreateRecipeEvent
}
