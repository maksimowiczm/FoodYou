package com.maksimowiczm.foodyou.feature.food.domain

import kotlinx.datetime.LocalDateTime

sealed interface ProductEvent {
    val date: LocalDateTime

    sealed interface ProductCreationEvent : ProductEvent

    class Created(override val date: LocalDateTime) :
        ProductEvent,
        ProductCreationEvent

    class Downloaded(override val date: LocalDateTime, val url: String?) :
        ProductEvent,
        ProductCreationEvent

    class Imported(override val date: LocalDateTime) :
        ProductEvent,
        ProductCreationEvent

    class Edited(override val date: LocalDateTime, val oldProduct: Product) : ProductEvent
}
