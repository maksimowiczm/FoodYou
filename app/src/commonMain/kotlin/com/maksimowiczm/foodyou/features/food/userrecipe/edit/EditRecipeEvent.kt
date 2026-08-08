package com.maksimowiczm.foodyou.features.food.userrecipe.edit

sealed interface EditRecipeEvent {
    data object Updated : EditRecipeEvent
}
