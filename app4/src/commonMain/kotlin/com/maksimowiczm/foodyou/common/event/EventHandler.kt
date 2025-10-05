package com.maksimowiczm.foodyou.common.event

interface EventHandler<E : DomainEvent> {
    suspend fun handle(event: E)
}
