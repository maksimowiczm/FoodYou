package com.maksimowiczm.foodyou.features.food.details.userrecipe

internal sealed interface UserRecipeDetailsUiEvent {
    data object Deleted : UserRecipeDetailsUiEvent
}
