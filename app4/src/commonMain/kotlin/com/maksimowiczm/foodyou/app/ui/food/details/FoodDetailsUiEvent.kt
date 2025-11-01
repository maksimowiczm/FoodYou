package com.maksimowiczm.foodyou.app.ui.food.details

sealed interface FoodDetailsUiEvent {

    /** Indicates that the food item has been deleted. */
    data object Deleted : FoodDetailsUiEvent
}
