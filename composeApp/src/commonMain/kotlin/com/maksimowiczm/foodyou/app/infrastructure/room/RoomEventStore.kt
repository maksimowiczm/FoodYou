package com.maksimowiczm.foodyou.app.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.event.DomainEvent
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

    override suspend fun append(stream: String, event: Iterable<DomainEvent>) {
        val roomEvents = event.map { mapper.toRoomStoredEventEntity(it, stream) }
        eventStoreDao.insertAll(roomEvents)
    }
}
