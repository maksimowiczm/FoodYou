package com.maksimowiczm.foodyou.features.food.userproduct.edit

internal sealed interface EditProductEvent {
    data object Edited : EditProductEvent
}
