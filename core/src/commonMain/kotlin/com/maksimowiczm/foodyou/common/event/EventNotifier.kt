package com.maksimowiczm.foodyou.common.event

import com.maksimowiczm.foodyou.common.EventSink
import com.maksimowiczm.foodyou.common.EventSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

/**
 * A live broadcast channel for [DomainEvent]s: publish an event, and any current subscriber can
 * observe it via [events] as it happens.
 *
 * Unlike [EventSource], which is queried per-stream for events of a specific type, [EventNotifier]
 * has no notion of streams.
 */
interface EventNotifier {
    suspend fun notify(event: DomainEvent)

    suspend fun notify(events: Iterable<DomainEvent>)

    val events: Flow<DomainEvent>
}

inline fun <reified E> EventNotifier.subscribe(
    coroutineScope: CoroutineScope,
    handler: EventHandler<E>,
) = coroutineScope.launch { events.filterIsInstance<E>().collect(handler::handle) }

/**
 * Adapts [EventNotifier] into an [EventSink] by [notify]-ing each event, ignoring the stream id.
 *
 * Useful for feeding an [EventNotifier] into code that expects an [EventSink], e.g. composing it
 * alongside a persistence-backed sink. Since [EventNotifier] has no notion of streams, the `stream`
 * argument passed to the resulting [EventSink.publish] is discarded.
 */
fun EventNotifier.asEventSink() = EventSink { _, events -> notify(events) }
