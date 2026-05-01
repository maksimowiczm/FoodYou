package com.maksimowiczm.foodyou.analytics.application

import com.maksimowiczm.foodyou.analytics.domain.Analytics
import com.maksimowiczm.foodyou.analytics.domain.AnalyticsEvent
import com.maksimowiczm.foodyou.analytics.domain.toAnalytics
import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.domain.load

class AnalyticsService(private val eventStore: EventStore) {
    suspend fun update(block: Analytics.() -> List<AnalyticsEvent>) {
        val analytics = eventStore.load<AnalyticsEvent>(EVENT_STREAM).toAnalytics()
        val newEvents = analytics.block()
        if (newEvents.isNotEmpty()) {
            eventStore.append(EVENT_STREAM, newEvents)
        }
    }

    private companion object {
        private const val EVENT_STREAM = "Analytics"
    }
}
