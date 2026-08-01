package com.maksimowiczm.foodyou.app.ui.food.details.userproduct

internal sealed interface UserProductDetailsUiEvent {
    data object Deleted : UserProductDetailsUiEvent
}
