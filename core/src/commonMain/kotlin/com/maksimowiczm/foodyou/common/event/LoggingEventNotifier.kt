package com.maksimowiczm.foodyou.common.event

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class LoggingEventNotifier(private val delegate: EventNotifier, logger: Logger) : EventNotifier {
    private val logger = logger.withTag("EventBus")

    override val events: Flow<DomainEvent> =
        delegate.events.onEach { event ->
            logger.d { "Event received: ${event::class.simpleName} | $event" }
        }

    override suspend fun notify(event: DomainEvent) {
        logger.d { "Publishing event: ${event::class.simpleName} | $event" }
        delegate.notify(event)
    }

    override suspend fun notify(events: Iterable<DomainEvent>) {
        val events = events as? Collection ?: events.toList()
        logger.d {
            buildString {
                appendLine(
                    "Publishing ${events.size} events: ${events.map { it::class.simpleName }}"
                )
                events.forEachIndexed { i, event -> appendLine("[$i] $event") }
            }
        }
        delegate.notify(events)
    }
}
