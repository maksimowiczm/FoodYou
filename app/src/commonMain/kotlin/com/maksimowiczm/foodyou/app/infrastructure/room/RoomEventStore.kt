package com.maksimowiczm.foodyou.app.infrastructure.room

import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.common.event.EventStore
import kotlin.time.Clock
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomEventStore(private val eventStoreDao: EventStoreDao) : EventStore {
    private val mapper = DomainEventMapper

    override suspend fun load(stream: String): Iterable<DomainEvent> =
        eventStoreDao.getAllByStream(stream).map(mapper::toDomainEvent)

    override fun observe(stream: String): Flow<Iterable<DomainEvent>> =
        eventStoreDao.observeAllByStream(stream).map { entities ->
            entities.map(mapper::toDomainEvent)
        }

    override suspend fun publish(
        stream: String,
        events: Iterable<DomainEvent>,
    ) {
        val now = Clock.System.now()
        val roomEvents = events.map {
            mapper.toRoomStoredEventEntity(
                event = it,
                stream = stream,
                dbId = Uuid.random(),
                persistedAtTimestamp = now,
            )
        }
        eventStoreDao.insertAll(roomEvents)
    }
}
