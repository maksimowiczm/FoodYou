package com.maksimowiczm.foodyou.feature.recipe.ui.update

internal sealed interface UpdateRecipeEvent {
    data object RecipeUpdated : UpdateRecipeEvent
}
