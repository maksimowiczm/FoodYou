package com.maksimowiczm.foodyou.feature.diary.recipe.ui.update

internal sealed interface UpdateRecipeEvent {
    data object RecipeUpdated : UpdateRecipeEvent
}
