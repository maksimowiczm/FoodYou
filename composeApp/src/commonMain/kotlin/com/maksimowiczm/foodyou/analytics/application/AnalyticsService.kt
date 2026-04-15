package com.maksimowiczm.foodyou.analytics.application

import com.maksimowiczm.foodyou.analytics.domain.AnalyticsEvent
import com.maksimowiczm.foodyou.analytics.domain.recordAppLaunch
import com.maksimowiczm.foodyou.analytics.domain.toAnalytics
import com.maksimowiczm.foodyou.common.application.AppConfig
import com.maksimowiczm.foodyou.common.application.EventStore
import com.maksimowiczm.foodyou.common.application.load
import kotlin.time.Clock

/**
 * Use case for recording application launch events in the analytics system.
 *
 * This use case is responsible for tracking when users launch the application, capturing relevant
 * metadata such as the app version, and persisting this information for analytics purposes.
 */
class AnalyticsService(
    private val clock: Clock,
    private val appConfig: AppConfig,
    private val eventStore: EventStore,
) {
    suspend fun recordAppLaunch() {
        val analytics = eventStore.load<AnalyticsEvent>(EVENT_STREAM).toAnalytics()
        val newEvents =
            analytics.recordAppLaunch(versionName = appConfig.versionName, clock = clock)
        eventStore.append(EVENT_STREAM, newEvents)
    }

    private companion object {
        private const val EVENT_STREAM = "Analytics"
    }
}
