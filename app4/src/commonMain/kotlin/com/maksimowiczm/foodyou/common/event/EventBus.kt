package com.maksimowiczm.foodyou.common.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

interface EventBus<E> {
    val events: Flow<E>

    suspend fun publish(event: E)

    suspend fun publish(events: List<E>)
}

inline fun <reified E> EventBus<E>.subscribe(
    coroutineScope: CoroutineScope,
    handler: EventHandler<E>,
) = coroutineScope.launch { events.filterIsInstance<E>().collect(handler::handle) }

context(coroutineScope: CoroutineScope)
inline fun <reified E> EventBus<E>.subscribe(handler: EventHandler<E>) =
    subscribe(coroutineScope, handler)
