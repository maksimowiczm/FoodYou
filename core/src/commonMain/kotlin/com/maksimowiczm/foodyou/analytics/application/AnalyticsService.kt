package com.maksimowiczm.foodyou.analytics.application

import com.maksimowiczm.foodyou.analytics.domain.Analytics
import com.maksimowiczm.foodyou.analytics.domain.AnalyticsCommand
import com.maksimowiczm.foodyou.analytics.domain.AnalyticsEvent
import com.maksimowiczm.foodyou.analytics.domain.analyticsDecider
import com.maksimowiczm.foodyou.analytics.domain.toAnalytics
import com.maksimowiczm.foodyou.common.event.EventNotifier
import com.maksimowiczm.foodyou.common.event.EventStore
import com.maksimowiczm.foodyou.common.event.asEventSink
import com.maksimowiczm.foodyou.common.event.asHandler
import com.maksimowiczm.foodyou.common.observe
import com.maksimowiczm.foodyou.common.plus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnalyticsService(
    private val eventStore: EventStore,
    eventNotifier: EventNotifier,
) {
    private val commandHandler =
        analyticsDecider.asHandler(
            eventStore,
            eventStore + eventNotifier.asEventSink(),
        )

    suspend fun handle(command: AnalyticsCommand) {
        val _ = commandHandler(STREAM, command)
    }

    fun observe(): Flow<Analytics?> =
        eventStore.observe<AnalyticsEvent>(STREAM).map { events ->
            if (events.none()) null else events.toAnalytics()
        }

    private companion object {
        private const val STREAM = "Analytics"
    }
}
