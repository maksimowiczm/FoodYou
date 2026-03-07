package com.maksimowiczm.foodyou.app.ui.food.details.userproduct

internal sealed interface UserProductDetailsUiEvent {

    /** Indicates that the food item has been deleted. */
    data object Deleted : UserProductDetailsUiEvent
}
