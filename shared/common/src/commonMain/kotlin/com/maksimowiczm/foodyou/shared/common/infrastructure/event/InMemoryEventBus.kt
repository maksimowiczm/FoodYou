package com.maksimowiczm.foodyou.shared.common.infrastructure.event

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.Event
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.EventBus
import com.maksimowiczm.foodyou.shared.common.log.FoodYouLogger
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

// Why is it so different to the command bus and query bus?
//
// The event bus is used to publish events that may be handled by multiple handlers or none at all.
// Currently, Koin doesn’t handle generics well, so implementing this cleanly would probably be a
// pain.
// The command handlers and query handlers were already tricky enough to implement in Koin.
//
// Additionally, event handlers will most likely run in the application-wide scope, so they must be
// singletons.
//
// They can also be used in view models, though I’m not sure if that’s a good idea—but it does work
// flawlessly.

internal class InMemoryEventBus : EventBus {

    // Sometimes it is possible that there will more events published than can be handled. We don't
    // want to suspend the publisher, so we use a shared flow with a buffer. If we still overflow
    // the buffer, drop the event that cannot be handled.
    //
    // It's not ideal but it is a trade-off that we have to make.
    private val _events =
        MutableSharedFlow<Event>(
            extraBufferCapacity = 50,
            onBufferOverflow = BufferOverflow.DROP_LATEST,
        )

    override val events: Flow<Event> = _events.asSharedFlow()

    override fun publish(event: Event) {
        if (_events.tryEmit(event)) {
            FoodYouLogger.w(TAG) { "Failed to publish event: $event" }
        } else {
            FoodYouLogger.d(TAG) { "Published event: $event" }
        }
    }

    private companion object {
        private const val TAG = "InMemoryEventBus"
    }
}
