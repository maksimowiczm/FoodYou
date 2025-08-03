package com.maksimowiczm.foodyou.feature.food.ui.product.update

internal sealed interface UpdateProductEvent {

    data object Updated : UpdateProductEvent
}
