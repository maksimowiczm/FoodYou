package com.maksimowiczm.foodyou.feature.food.ui.recipe.update

internal sealed interface UpdateRecipeEvent {
    data object Updated : UpdateRecipeEvent
}
