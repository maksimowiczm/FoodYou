package com.maksimowiczm.foodyou.capabilities.fooddetails.userproduct

sealed interface UserProductDetailsUiEvent {
    data object Deleted : UserProductDetailsUiEvent
}
