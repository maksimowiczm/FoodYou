package com.maksimowiczm.foodyou.common.event

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class LoggingEventBus(private val delegate: EventBus, logger: Logger) : EventBus {
    private val logger = logger.withTag("EventBus")

    override val events: Flow<DomainEvent> =
        delegate.events.onEach { event ->
            logger.d { "Event received: ${event::class.simpleName} | $event" }
        }

    override suspend fun publish(event: DomainEvent) {
        logger.d { "Publishing event: ${event::class.simpleName} | $event" }
        delegate.publish(event)
    }

    override suspend fun publish(events: List<DomainEvent>) {
        logger.d {
            buildString {
                appendLine(
                    "Publishing ${events.size} events: ${events.map { it::class.simpleName }}"
                )
                events.forEachIndexed { i, event ->
                    appendLine("[$i] $event")
                }
            }
        }
        delegate.publish(events)
    }
}
