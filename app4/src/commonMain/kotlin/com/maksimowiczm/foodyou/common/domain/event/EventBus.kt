package com.maksimowiczm.foodyou.common.domain.event

import kotlinx.coroutines.flow.Flow

interface EventBus {
    val events: Flow<DomainEvent>

    suspend fun publish(event: DomainEvent)
}
