package com.maksimowiczm.foodyou.analytics.infrastructure

import com.maksimowiczm.foodyou.analytics.domain.Analytics
import com.maksimowiczm.foodyou.analytics.domain.AnalyticsEvent
import com.maksimowiczm.foodyou.analytics.domain.AnalyticsRepository
import com.maksimowiczm.foodyou.common.infrastructure.room.eventstore.AbstractRoomEventSourcedRepository
import com.maksimowiczm.foodyou.common.infrastructure.room.eventstore.EventStoreDao

internal class AnalyticsRepositoryImpl(eventStoreDao: EventStoreDao) :
    AbstractRoomEventSourcedRepository<Analytics, AnalyticsEvent>(eventStoreDao),
    AnalyticsRepository {
    override val streamClass = AnalyticsEvent::class

    override fun factory(events: List<AnalyticsEvent>) = Analytics.replayFrom(events)
}
