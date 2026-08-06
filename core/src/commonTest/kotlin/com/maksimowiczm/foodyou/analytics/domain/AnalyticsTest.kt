package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.clock.staticClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class AnalyticsTest {

    private val t0 = Instant.fromEpochSeconds(1_600_000_000)
    private val t1 = Instant.fromEpochSeconds(1_700_000_000)

    @Test
    fun recordAppLaunch_whenFirstLaunch_thenEmitsThreeEvents() {
        val analytics = Analytics()

        val events = analytics.recordAppLaunch("1.0.0", staticClock(t0)).toList()

        assertEquals(3, events.size)

        assertEquals(AppLaunchedEvent("1.0.0", t0), events[0])

        assertEquals(FirstAppLaunchRecordedEvent("1.0.0", t0), events[1])

        assertEquals(AppVersionChangedEvent("1.0.0", t0), events[2])
    }

    @Test
    fun recordAppLaunch_whenSameVersion_thenEmitsOnlyLaunchEvent() {
        val analytics =
            Analytics(
                appFirstLaunchAt = t0,
                appFirstLaunchVersion = "1.0.0",
                currentVersion = "1.0.0",
                currentVersionFirstLaunchAt = t0,
                launchCount = 1,
            )

        val events = analytics.recordAppLaunch("1.0.0", staticClock(t1)).toList()

        assertEquals(1, events.size)

        assertEquals(AppLaunchedEvent("1.0.0", t1), events[0])
    }

    @Test
    fun recordAppLaunch_whenVersionChanges_thenEmitsVersionChangedEvent() {
        val analytics =
            Analytics(
                appFirstLaunchAt = t0,
                appFirstLaunchVersion = "1.0.0",
                currentVersion = "1.0.0",
                currentVersionFirstLaunchAt = t0,
                launchCount = 1,
            )

        val events = analytics.recordAppLaunch("2.0.0", staticClock(t1)).toList()

        assertEquals(2, events.size)

        assertEquals(AppLaunchedEvent("2.0.0", t1), events[0])

        assertEquals(AppVersionChangedEvent("2.0.0", t1), events[1])
    }

    @Test
    fun apply_whenAppLaunchedEvent_thenIncrementsLaunchCount() {
        val analytics = Analytics(launchCount = 5)

        val result = analytics.apply(AppLaunchedEvent("1.0.0", t0))

        assertEquals(6, result.launchCount)
    }

    @Test
    fun apply_whenFirstAppLaunchRecordedEvent_thenSetsFirstLaunchFields() {
        val analytics = Analytics()

        val result = analytics.apply(FirstAppLaunchRecordedEvent("1.0.0", t0))

        assertEquals(t0, result.appFirstLaunchAt)
        assertEquals("1.0.0", result.appFirstLaunchVersion)
    }

    @Test
    fun apply_whenAppVersionChangedEvent_thenUpdatesCurrentVersion() {
        val analytics = Analytics()

        val result = analytics.apply(AppVersionChangedEvent("2.0.0", t1))

        assertEquals("2.0.0", result.currentVersion)
        assertEquals(t1, result.currentVersionFirstLaunchAt)
    }

    @Test
    fun toAnalytics_whenEventsProvided_thenRebuildsState() {
        val events =
            listOf(
                AppLaunchedEvent("1.0.0", t0),
                FirstAppLaunchRecordedEvent("1.0.0", t0),
                AppVersionChangedEvent("1.0.0", t0),
                AppLaunchedEvent("2.0.0", t1),
                AppVersionChangedEvent("2.0.0", t1),
            )

        val analytics = events.toAnalytics()

        assertEquals(t0, analytics.appFirstLaunchAt)
        assertEquals("1.0.0", analytics.appFirstLaunchVersion)
        assertEquals("2.0.0", analytics.currentVersion)
        assertEquals(t1, analytics.currentVersionFirstLaunchAt)
        assertEquals(2, analytics.launchCount)
    }

    @Test
    fun toAnalytics_whenEmptyEvents_thenReturnsDefaultState() {
        val analytics = emptyList<AnalyticsEvent>().toAnalytics()

        assertNull(analytics.appFirstLaunchAt)
        assertNull(analytics.appFirstLaunchVersion)
        assertNull(analytics.currentVersion)
        assertNull(analytics.currentVersionFirstLaunchAt)
        assertEquals(0, analytics.launchCount)
    }
}
