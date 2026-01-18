package com.maksimowiczm.foodyou.common.event

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * In-memory implementation of EventBus using Kotlin Flow.
 *
 * This implementation stores events in memory with a large buffer capacity, suspending on overflow
 * to prevent event loss. Events are not replayed to new subscribers.
 *
 * @param E The type of events this bus handles
 */
class InMemoryEventBus<E> : EventBus<E> {
    private val _events =
        MutableSharedFlow<E>(
            replay = 0,
            extraBufferCapacity = Int.MAX_VALUE,
            onBufferOverflow = BufferOverflow.SUSPEND,
        )

    override val events: Flow<E> = _events.asSharedFlow()

    override suspend fun publish(event: E) {
        _events.emit(event)
    }

    override suspend fun publish(events: List<E>) {
        events.forEach { _events.emit(it) }
    }
}
