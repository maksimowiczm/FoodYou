package com.maksimowiczm.foodyou.common.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent

/**
 * Base class for Aggregate Roots in a Domain-Driven Design model.
 *
 * An aggregate root is responsible for maintaining consistency boundaries and emitting
 * [DomainEvent]s that describe state changes. Events raised through [raise] are first applied to
 * mutate the aggregate state and then stored in the internal event list for later dispatching.
 *
 * @param E type of [DomainEvent] handled and produced by this aggregate
 */
abstract class AggregateRoot<E : DomainEvent> {

    private val _events = mutableListOf<E>()

    /**
     * Immutable snapshot of events raised by this aggregate.
     *
     * Typically consumed by an application layer or event dispatcher after executing a command.
     */
    val events: List<E>
        get() = _events.toList()

    /**
     * Raises a new domain [event].
     *
     * The event is first applied to mutate the aggregate state via [apply], then appended to the
     * internal event list.
     *
     * @param event event describing a state change
     */
    protected fun raise(event: E) {
        apply(event)
        _events.add(event)
    }

    /**
     * Applies the given [event] to mutate the aggregate state.
     *
     * Implementations should be pure state transitions and must not raise additional events
     * directly. This method is invoked internally by [raise].
     *
     * @param event event to apply
     */
    protected abstract fun apply(event: E)
}
