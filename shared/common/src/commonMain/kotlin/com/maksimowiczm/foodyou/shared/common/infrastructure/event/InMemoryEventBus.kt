package com.maksimowiczm.foodyou.shared.common.infrastructure.event

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.Event
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.EventBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.EventHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class InMemoryEventBus(
    eventHandlers: List<EventHandler<Event>>,
    private val scope: CoroutineScope,
) : EventBus {

    private val commandHandlers: Map<KClass<*>, List<EventHandler<*>>> =
        eventHandlers.groupBy { it.eventType }

    override suspend fun <E : Event> publish(event: E) {
        @Suppress("UNCHECKED_CAST")
        val handlers =
            commandHandlers[event::class] as? List<EventHandler<E>>
                ?: error("No handlers found for event: ${event::class.simpleName}")

        // Run in parallel
        handlers.forEach { handler -> scope.launch { handler.handle(event) } }
    }
}
