package com.maksimowiczm.foodyou.common.event

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class InMemoryEventBus : EventBus {
    private val _events =
        MutableSharedFlow<DomainEvent>(
            replay = 0,
            extraBufferCapacity = Int.MAX_VALUE,
            onBufferOverflow = BufferOverflow.SUSPEND,
        )

    override val events: Flow<DomainEvent> = _events.asSharedFlow()

    override suspend fun publish(event: DomainEvent) {
        _events.emit(event)
    }

    override suspend fun publish(events: List<DomainEvent>) {
        events.forEach { _events.emit(it) }
    }
}
