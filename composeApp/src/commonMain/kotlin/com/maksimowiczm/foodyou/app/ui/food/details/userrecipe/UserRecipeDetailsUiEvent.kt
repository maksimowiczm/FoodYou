package com.maksimowiczm.foodyou.app.ui.food.details.userrecipe

internal sealed interface UserRecipeDetailsUiEvent {
    data object Deleted : UserRecipeDetailsUiEvent
}
