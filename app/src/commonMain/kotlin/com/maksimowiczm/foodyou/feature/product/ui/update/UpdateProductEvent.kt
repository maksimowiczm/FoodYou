package com.maksimowiczm.foodyou.feature.product.ui.update

internal sealed interface UpdateProductEvent {
    data object Updated : UpdateProductEvent
}
