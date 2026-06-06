package com.maksimowiczm.foodyou.app.ui.food.recipe.edit

sealed interface EditRecipeEvent {
    data object Updated : EditRecipeEvent
}
