package com.maksimowiczm.foodyou.common.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

interface EventBus {
    val events: Flow<DomainEvent>

    suspend fun publish(event: DomainEvent)

    suspend fun publish(events: List<DomainEvent>)
}

inline fun <reified E : DomainEvent> EventBus.subscribe(
    coroutineScope: CoroutineScope,
    handler: EventHandler<E>,
) = coroutineScope.launch { events.filterIsInstance<E>().collect(handler::handle) }

context(coroutineScope: CoroutineScope)
inline fun <reified E : DomainEvent> EventBus.subscribe(handler: EventHandler<E>) =
    subscribe(coroutineScope, handler)
