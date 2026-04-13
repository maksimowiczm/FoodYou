package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.clock.staticClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class AnalyticsTest {
    @Test
    fun recordAppLaunch_firstLaunch_setsFirstLaunchEverAndVersion() {
        val now = Instant.fromEpochSeconds(1_600_000_000)
        val analytics = Analytics.of()

        analytics.recordAppLaunch(versionName = "1.0.0", clock = staticClock(now))

        assertEquals(now, analytics.firstLaunchEver)
        assertEquals("1.0.0", analytics.firstLaunchEverVersionName)
        assertEquals("1.0.0", analytics.currentVersion)
        assertEquals(now, analytics.firstLaunchCurrentVersion)
        assertEquals("1.0.0", analytics.firstLaunchCurrentVersionName)
        assertEquals(1, analytics.launchCount)
    }

    @Test
    fun recordAppLaunch_sameVersion_incrementsLaunchCountOnly() {
        val now = Instant.fromEpochSeconds(1_600_000_000)
        val analytics = Analytics.of()

        analytics.recordAppLaunch("1.0.0", staticClock(now))
        analytics.recordAppLaunch("1.0.0", staticClock(now))

        assertEquals(2, analytics.launchCount)
        assertEquals("1.0.0", analytics.currentVersion)
        assertEquals(now, analytics.firstLaunchEver)
    }

    @Test
    fun recordAppLaunch_versionChanged_updatesCurrentVersion() {
        val t1 = Instant.fromEpochSeconds(1_600_000_000)
        val t2 = Instant.fromEpochSeconds(1_700_000_000)

        val analytics = Analytics.of()

        analytics.recordAppLaunch("1.0.0", staticClock(t1))
        analytics.recordAppLaunch("2.0.0", staticClock(t2))

        assertEquals("2.0.0", analytics.currentVersion)
        assertEquals(t2, analytics.firstLaunchCurrentVersion)
        assertEquals("2.0.0", analytics.firstLaunchCurrentVersionName)
        assertEquals(2, analytics.launchCount)
    }

    @Test
    fun replayFrom_firstAppLaunchEvent_setsFirstLaunchEver() {
        val now = Instant.fromEpochSeconds(1_600_000_000)

        val analytics =
            Analytics.replayFrom(
                listOf(FirstAppLaunchRecordedEvent(versionName = "1.0.0", timestamp = now))
            )

        assertEquals(now, analytics.firstLaunchEver)
        assertEquals("1.0.0", analytics.firstLaunchEverVersionName)
    }

    @Test
    fun replayFrom_versionChangedEvent_updatesCurrentVersion() {
        val now = Instant.fromEpochSeconds(1_600_000_000)

        val analytics =
            Analytics.replayFrom(
                listOf(AppVersionChangedEvent(newVersionName = "2.0.0", timestamp = now))
            )

        assertEquals("2.0.0", analytics.currentVersion)
        assertEquals(now, analytics.firstLaunchCurrentVersion)
        assertEquals("2.0.0", analytics.firstLaunchCurrentVersionName)
    }

    @Test
    fun replayFrom_appLaunchedEvents_incrementsLaunchCount() {
        val now = Instant.fromEpochSeconds(1_600_000_000)

        val analytics =
            Analytics.replayFrom(
                listOf(
                    AppLaunchedEvent("1.0.0", now),
                    AppLaunchedEvent("1.0.0", now),
                    AppLaunchedEvent("1.0.0", now),
                )
            )

        assertEquals(3, analytics.launchCount)
    }
}
