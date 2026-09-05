package com.maksimowiczm.foodyou.common

import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.common.event.EventStore
import com.maksimowiczm.foodyou.common.event.load

/**
 * Encapsulates the decision logic of an event-sourced aggregate, following the "decider" pattern: a
 * pure, side-effect-free description of how commands turn into events, and how events fold into
 * state.
 *
 * @param C the command type accepted by this decider
 * @param E the domain event type produced by this decider
 * @param S the state type derived by folding events
 * @property decide given the current state and an incoming command, returns the events that should
 *   be recorded.
 * @property evolve folds a single event into the state, producing the next state.
 * @property initialState the state used before any events have been applied, i.e. the state of a
 *   stream that does not exist yet.
 */
class Decider<C, E, S>(
    val decide: (C, S) -> List<E>,
    val evolve: (S, E) -> S,
    val initialState: S,
)

/**
 * A runnable command handler for a single event-sourced stream: given a stream id and a command,
 * loads history, decides, persists, and returns the events that were appended.
 *
 * @param C the command type accepted by the handler
 * @param E the domain event type it may append
 */
typealias CommandHandler<C, E> = suspend (String, C) -> List<E>

/**
 * Binds this [Decider] to an [eventStore] (and optional [extensions]), producing a [CommandHandler]
 * that can be invoked per command.
 *
 * @param eventStore the store used to load prior events for a stream and append newly decided
 *   events
 * @param extensions sinks that are notified with the newly appended events after a successful
 *   append, e.g. for publishing to a message bus
 * @return a [CommandHandler] that loads state, decides, persists, and notifies extensions each time
 *   it is invoked
 */
inline fun <C, reified E : DomainEvent, S> Decider<C, E, S>.asHandler(
    eventStore: EventStore,
    vararg extensions: EventSink,
): CommandHandler<C, E> = WithEventStore(eventStore = eventStore, *extensions).asHandler(this)

/**
 * Bundles an [EventStore] with a set of [EventSink] extensions so that multiple [Decider]s can be
 * turned into [CommandHandler]s without re-specifying the store and extensions each time.
 *
 * @property eventStore the store used to load and append events
 * @property extensions sinks notified with newly appended events after each successful append
 */
class WithEventStore(
    val eventStore: EventStore,
    vararg val extensions: EventSink,
) {
    /**
     * Turns [decider] into a [CommandHandler] bound to this instance's [eventStore] and
     * [extensions].
     */
    inline fun <C, reified E : DomainEvent, S> asHandler(
        decider: Decider<C, E, S>
    ): CommandHandler<C, E> = { stream, command ->
        val events = eventStore.load<E>(stream)
        val state = events.fold(decider.initialState) { s, e -> decider.evolve(s, e) }
        val newEvents = decider.decide(command, state)

        eventStore.append(stream, newEvents)

        extensions.forEach { it.publish(newEvents) }

        newEvents
    }
}
