package com.maksimowiczm.foodyou.capabilities.fooddetails.userrecipe

sealed interface UserRecipeDetailsUiEvent {
    data object Deleted : UserRecipeDetailsUiEvent
}
