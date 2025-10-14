package com.maksimowiczm.foodyou.analytics.infrastructure

import com.maksimowiczm.foodyou.analytics.domain.AccountAnalytics
import com.maksimowiczm.foodyou.analytics.domain.AccountAnalyticsRepository
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.infrastructure.room.eventstore.EventStoreDao
import com.maksimowiczm.foodyou.common.infrastructure.room.eventstore.RoomEventStoreMapper

class AccountAnalyticsRepositoryImpl(private val eventStoreDao: EventStoreDao) :
    AccountAnalyticsRepository {

    private val mapper = RoomEventStoreMapper

    override suspend fun load(localAccountId: LocalAccountId): AccountAnalytics {
        val roomEvents = eventStoreDao.getAllByAggregateId(localAccountId.value)
        val events = roomEvents.map { mapper.toDomainEvent(it) }

        val accountAnalytics = AccountAnalytics.of(localAccountId)
        events.forEach { accountAnalytics.apply(it) }

        return accountAnalytics
    }

    override suspend fun save(accountAnalytics: AccountAnalytics) {
        val events = accountAnalytics.events
        val roomEvents = events.map { mapper.toRoomEventStoreEntity(it) }
        eventStoreDao.insertAll(roomEvents)
    }
}
