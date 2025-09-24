package com.maksimowiczm.foodyou.app.ui.food.recipe

internal sealed interface UpdateRecipeEvent {
    data object Updated : UpdateRecipeEvent
}
