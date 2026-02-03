package com.maksimowiczm.foodyou.app.ui.food.details.userfood

internal sealed interface UserFoodDetailsUiEvent {

    /** Indicates that the food item has been deleted. */
    data object Deleted : UserFoodDetailsUiEvent
}
