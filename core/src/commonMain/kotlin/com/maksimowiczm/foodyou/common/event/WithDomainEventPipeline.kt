package com.maksimowiczm.foodyou.common.event

import com.maksimowiczm.foodyou.common.Decider
import com.maksimowiczm.foodyou.common.EventSink
import com.maksimowiczm.foodyou.common.EventSource
import com.maksimowiczm.foodyou.common.load

/**
 * A runnable command handler for a single event-sourced stream: given a stream id and a command,
 * loads history, decides, persists, and returns the events that were appended.
 *
 * @param C the command type accepted by the handler
 * @param E the domain event type it may append
 */
private typealias CommandHandler<C, E> = suspend (String, C) -> List<E>

/**
 * Wires this [Decider] into a runnable [CommandHandler] by pairing it with an [EventSource] to
 * obtain the stream's existing events and an [EventSink] to publish newly decided events.
 *
 * @param C the command type accepted by the resulting handler
 * @param E the domain event type read from [source] and published to [sink]
 * @param S the state type derived by folding events
 * @param source the [EventSource] queried for the stream's existing events
 * @param sink the [EventSink] that newly decided events are published to
 * @return a [CommandHandler] built from this decider and the given source/sink pair
 */
inline fun <C, reified E : DomainEvent, S> Decider<C, E, S>.asHandler(
    source: EventSource<DomainEvent>,
    sink: EventSink<DomainEvent>,
): CommandHandler<C, E> = WithDomainEventPipeline(source, sink).asHandler<C, E, S>(this)

/**
 * Bundles an [EventSource] and [EventSink] into a reusable pipeline for turning [Decider]s into
 * runnable [CommandHandler]s.
 *
 * @property source the [EventSource] queried for a stream's existing events
 * @property sink the [EventSink] that events decided by a [Decider] are published to
 */
class WithDomainEventPipeline(
    val source: EventSource<DomainEvent>,
    val sink: EventSink<DomainEvent>,
) {
    /**
     * Builds a [CommandHandler] for the given [decider] using this pipeline's [source] and [sink].
     *
     * On each invocation, the handler:
     * 1. obtains the stream's existing events of type [E] from [source],
     * 2. folds them into state via [Decider.evolve], starting from [Decider.initialState],
     * 3. calls [Decider.decide] with the resulting state and the incoming command,
     * 4. publishes any resulting events to [sink],
     * 5. returns the newly decided events.
     *
     * @param C the command type accepted by the handler
     * @param E the domain event type read from [source] and published to [sink]
     * @param S the state type derived by folding events
     * @param decider the decision logic to run against the folded state
     * @return a [CommandHandler] wired to this pipeline's [source] and [sink]
     */
    inline fun <C, reified E : DomainEvent, S> asHandler(
        decider: Decider<C, E, S>
    ): CommandHandler<C, E> = { stream, command ->
        val events = source.load<E>(stream)
        val state = events.fold(decider.initialState) { s, e -> decider.evolve(s, e) }
        val newEvents = decider.decide(command, state)

        sink.publish(stream, newEvents)

        newEvents
    }
}
