package com.maksimowiczm.foodyou.feature.food.recipe.presentation

internal sealed interface UpdateRecipeEvent {
    data object Updated : UpdateRecipeEvent
}
