package com.maksimowiczm.foodyou.common.domain.event

interface EventHandler<E : DomainEvent> {
    suspend fun handle(event: E)
}
