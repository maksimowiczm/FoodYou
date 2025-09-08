package com.maksimowiczm.foodyou.shared.domain.event

fun interface EventHandler<E : DomainEvent> {
    suspend fun handle(event: E)
}
