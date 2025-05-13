package com.maksimowiczm.foodyou.feature.product.ui.create

internal sealed interface CreateProductEvent {
    data class Created(val id: Long) : CreateProductEvent
}
