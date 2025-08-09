package com.maksimowiczm.foodyou.feature.food.product.presentation.update

internal sealed interface UpdateProductEvent {

    data object Updated : UpdateProductEvent
}
