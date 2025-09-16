package com.maksimowiczm.foodyou.app.ui.food.product.update

internal sealed interface UpdateProductEvent {

    data object Updated : UpdateProductEvent
}
