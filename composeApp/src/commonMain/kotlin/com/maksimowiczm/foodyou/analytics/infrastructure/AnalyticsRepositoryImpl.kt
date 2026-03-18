package com.maksimowiczm.foodyou.analytics.infrastructure

import com.maksimowiczm.foodyou.analytics.domain.Analytics
import com.maksimowiczm.foodyou.analytics.domain.AnalyticsRepository
import com.maksimowiczm.foodyou.common.infrastructure.room.eventstore.EventStoreDao
import com.maksimowiczm.foodyou.common.infrastructure.room.eventstore.RoomEventStoreMapper

internal class AnalyticsRepositoryImpl(private val eventStoreDao: EventStoreDao) :
    AnalyticsRepository {

    private val mapper = RoomEventStoreMapper

    override suspend fun load(): Analytics {
        val roomEvents = eventStoreDao.getAllByAggregateId()
        val events = roomEvents.map(mapper::toDomainEvent)

        val analytics = Analytics()
        events.forEach(analytics::apply)

        return analytics
    }

    override suspend fun save(analytics: Analytics) {
        val roomEvents = analytics.events.map(mapper::toRoomEventStoreEntity)
        eventStoreDao.insertAll(roomEvents)
    }
}
