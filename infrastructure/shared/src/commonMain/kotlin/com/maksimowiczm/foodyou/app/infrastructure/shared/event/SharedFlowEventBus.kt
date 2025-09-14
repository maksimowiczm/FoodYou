package com.maksimowiczm.foodyou.app.infrastructure.shared.event

import com.maksimowiczm.foodyou.shared.domain.event.DomainEvent
import com.maksimowiczm.foodyou.shared.domain.event.EventBus
import com.maksimowiczm.foodyou.shared.domain.log.Logger
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class SharedFlowEventBus(private val logger: Logger) : EventBus {

    // Sometimes it is possible that there will more events published than can be handled. We don't
    // want to suspend the publisher, so we use a shared flow with a buffer. If we still overflow
    // the buffer, drop the event that cannot be handled.
    //
    // It's not ideal but it is a trade-off that we have to make.
    private val _events =
        MutableSharedFlow<DomainEvent>(
            extraBufferCapacity = 50,
            onBufferOverflow = BufferOverflow.DROP_LATEST,
        )

    override val events: Flow<DomainEvent> = _events.asSharedFlow()

    override fun publish(domainEvent: DomainEvent) {
        if (_events.tryEmit(domainEvent)) {
            logger.d(TAG) { "Published event: $domainEvent" }
        } else {
            logger.w(TAG) { "Failed to publish event: $domainEvent" }
        }
    }

    private companion object {
        private const val TAG = "SharedFlowEventBus"
    }
}
