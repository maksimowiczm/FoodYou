package com.maksimowiczm.foodyou.features.food.details.userproduct

internal sealed interface UserProductDetailsUiEvent {
    data object Deleted : UserProductDetailsUiEvent
}
