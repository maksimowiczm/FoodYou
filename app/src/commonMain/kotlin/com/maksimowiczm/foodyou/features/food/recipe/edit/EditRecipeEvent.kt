package com.maksimowiczm.foodyou.features.food.recipe.edit

sealed interface EditRecipeEvent {
    data object Updated : EditRecipeEvent
}
