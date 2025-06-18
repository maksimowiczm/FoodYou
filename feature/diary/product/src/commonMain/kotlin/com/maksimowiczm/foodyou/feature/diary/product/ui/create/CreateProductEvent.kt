package com.maksimowiczm.foodyou.feature.diary.product.ui.create

internal sealed interface CreateProductEvent {
    data class Created(val id: Long) : CreateProductEvent
}
