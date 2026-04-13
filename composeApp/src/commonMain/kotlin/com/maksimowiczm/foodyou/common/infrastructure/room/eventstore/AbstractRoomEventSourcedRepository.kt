package com.maksimowiczm.foodyou.common.infrastructure.room.eventstore

import com.maksimowiczm.foodyou.common.domain.AggregateRoot
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Base class for event-sourced repositories backed by Room.
 *
 * Manages persistence and rehydration of an [AggregateRoot] via a flat event log. Subclasses
 * declare which [DomainEvent] stream to listen to and how to reconstruct the aggregate from that
 * stream.
 *
 * @param A The aggregate root type produced and consumed by this repository.
 * @param E The domain-event type that makes up the aggregate's event stream.
 * @param eventStoreDao DAO used to read from and write to the underlying Room event-store table.
 */
abstract class AbstractRoomEventSourcedRepository<A : AggregateRoot<E>, E : DomainEvent>(
    private val eventStoreDao: EventStoreDao
) {

    /**
     * Kotlin class of the domain event type [E].
     *
     * Used to derive the stream name. Must have a simple (non-anonymous) name.
     */
    protected abstract val streamClass: KClass<E>

    /**
     * Reconstructs the aggregate from an ordered list of past events.
     *
     * Called every time events are loaded from or observed in the store. Implementations typically
     * fold the events through the aggregate's apply logic.
     *
     * @param events All stored events for this stream, in insertion order.
     * @return A fully rehydrated aggregate reflecting the given history.
     */
    protected abstract fun factory(events: List<E>): A

    /** Stream name derived from [streamClass]. Computed once on first access. */
    private val stream: String by
        lazy(LazyThreadSafetyMode.NONE) {
            streamClass.simpleName ?: error("$streamClass must have a name")
        }

    /**
     * Returns a cold [Flow] that emits a freshly rehydrated aggregate whenever the underlying event
     * stream changes.
     *
     * @return A flow of aggregates built from the complete event history at each emission point.
     */
    fun observe(): Flow<A> = eventStoreDao.observeAllByStream(stream).map { it.toAggregate() }

    /**
     * Reads the current state of the aggregate from the event store.
     *
     * Fetches all stored events for the stream in a single query and replays them through
     * [factory].
     *
     * @return The aggregate rehydrated from its complete event history.
     */
    suspend fun load(): A = eventStoreDao.getAllByStream(stream).toAggregate()

    /**
     * Persists any uncommitted events held by [aggregate].
     *
     * Only the events accumulated since the aggregate was last loaded or created are written; if
     * there are none, this function is a no-op.
     *
     * @param aggregate The aggregate whose pending events should be appended to the event store.
     */
    suspend fun save(aggregate: A) {
        val events = aggregate.events
        if (events.isEmpty()) return
        eventStoreDao.insertAll(
            events.map { RoomStoredEventMapper.toRoomStoredEventEntity(it, stream) }
        )
    }

    private fun Iterable<RoomStoredEventEntity>.toAggregate(): A {
        val events = map { RoomStoredEventMapper.toDomainEvent<E>(it) }
        return factory(events)
    }
}
