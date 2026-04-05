package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.clock.staticClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant

class AnalyticsTest {
    @Test
    fun recordAppLaunch_once() {
        val analytics = Analytics()
        val versionName = "4.0.0"
        val now = Instant.fromEpochSeconds(1_600_000_000)
        val clock = staticClock(now)

        analytics.recordAppLaunch(versionName = versionName, clock = clock)

        assertEquals(now, analytics.firstLaunchEver)
        assertEquals(versionName, analytics.firstLaunchEverVersionName)
        assertEquals(versionName, analytics.currentVersion)
        assertEquals(now, analytics.firstLaunchCurrentVersion)
        assertEquals(versionName, analytics.firstLaunchCurrentVersionName)
        assertEquals(1, analytics.launchCount)
    }

    @Test
    fun recordAppLaunch_multipleTimes() {
        val analytics = Analytics()
        val versionNameV1 = "4.0.0"
        val versionNameV2 = "4.1.0"
        val now1 = Instant.fromEpochSeconds(1_600_000_000)
        val now2 = Instant.fromEpochSeconds(1_600_000_100)
        val clock1 = staticClock(now1)
        val clock2 = staticClock(now2)

        analytics.recordAppLaunch(versionName = versionNameV1, clock = clock1)

        analytics.recordAppLaunch(versionName = versionNameV2, clock = clock2)

        assertEquals(now1, analytics.firstLaunchEver)
        assertEquals(versionNameV1, analytics.firstLaunchEverVersionName)
        assertEquals(versionNameV2, analytics.currentVersion)
        assertEquals(now2, analytics.firstLaunchCurrentVersion)
        assertEquals(versionNameV2, analytics.firstLaunchCurrentVersionName)
        assertEquals(2, analytics.launchCount)
    }

    @Test
    fun recordAppLaunch_raisesEvents() {
        val analytics = Analytics()
        val versionName = "4.0.0"
        val now = Instant.fromEpochSeconds(1_600_000_000)
        val clock = staticClock(now)

        analytics.recordAppLaunch(versionName = versionName, clock = clock)

        val events = analytics.events
        assertEquals(3, events.size)
        val _ = assertIs<AppLaunchedEvent>(events[0])
        val _ = assertIs<FirstAppLaunchRecordedEvent>(events[1])
        val _ = assertIs<AppVersionChangedEvent>(events[2])
    }

    @Test
    fun applyAppLaunchedEvent() {
        val analytics = Analytics()
        val versionName = "4.0.0"
        val now = Instant.fromEpochSeconds(1_600_000_000)
        val event = AppLaunchedEvent(versionName = versionName, timestamp = now)

        analytics.apply(event)

        assertEquals(1, analytics.launchCount)
    }

    @Test
    fun applyFirstAppLaunchRecordedEvent() {
        val analytics = Analytics()
        val versionName = "4.0.0"
        val now = Instant.fromEpochSeconds(1_600_000_000)
        val event = FirstAppLaunchRecordedEvent(versionName = versionName, timestamp = now)

        analytics.apply(event)

        assertEquals(now, analytics.firstLaunchEver)
        assertEquals(versionName, analytics.firstLaunchEverVersionName)
    }

    @Test
    fun applyAppVersionChangedEvent() {
        val analytics = Analytics()
        val versionName = "4.0.0"
        val now = Instant.fromEpochSeconds(1_600_000_000)
        val event = AppVersionChangedEvent(newVersionName = versionName, timestamp = now)

        analytics.apply(event)

        assertEquals(versionName, analytics.currentVersion)
        assertEquals(now, analytics.firstLaunchCurrentVersion)
        assertEquals(versionName, analytics.firstLaunchCurrentVersionName)
    }
}
