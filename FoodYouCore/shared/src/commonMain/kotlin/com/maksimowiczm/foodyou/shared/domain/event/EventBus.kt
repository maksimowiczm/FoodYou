package com.maksimowiczm.foodyou.shared.domain.event

import kotlinx.coroutines.flow.Flow

interface EventBus {

    val events: Flow<DomainEvent>

    fun publish(domainEvent: DomainEvent)
}
