package com.maksimowiczm.foodyou.analytics.infrastructure

import com.maksimowiczm.foodyou.analytics.domain.AccountAnalytics
import com.maksimowiczm.foodyou.analytics.domain.AccountAnalyticsRepository
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.infrastructure.room.eventstore.EventStoreDao
import com.maksimowiczm.foodyou.common.infrastructure.room.eventstore.RoomEventStoreMapper

internal class AccountAnalyticsRepositoryImpl(private val eventStoreDao: EventStoreDao) :
    AccountAnalyticsRepository {

    private val mapper = RoomEventStoreMapper

    override suspend fun load(localAccountId: LocalAccountId): AccountAnalytics {
        val roomEvents = eventStoreDao.getAllByAggregateId(localAccountId.value)
        val events = roomEvents.map(mapper::toDomainEvent)

        val accountAnalytics = AccountAnalytics.of(localAccountId)
        events.forEach(accountAnalytics::apply)

        return accountAnalytics
    }

    override suspend fun save(accountAnalytics: AccountAnalytics) {
        val roomEvents = accountAnalytics.events.map(mapper::toRoomEventStoreEntity)
        eventStoreDao.insertAll(roomEvents)
    }
}
