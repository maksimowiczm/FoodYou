package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.domain.testLocalAccountId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

class AccountAnalyticsTest {
    @Test
    fun recordAppLaunch_setFirstLaunchOnFirstLaunch() {
        val analytics =
            AccountAnalytics(
                ownerId = testLocalAccountId(),
                firstLaunchEver = null,
                firstLaunchEverVersionName = null,
                currentVersion = null,
                firstLaunchCurrentVersion = null,
                firstLaunchCurrentVersionName = null,
                launchCount = 0,
            )

        analytics.recordAppLaunch(versionName = "1.0.0", clock = Clock.System)

        assertEquals(1, analytics.launchCount)
        assertEquals("1.0.0", analytics.firstLaunchEverVersionName)
        assertEquals("1.0.0", analytics.currentVersion)
        assertEquals("1.0.0", analytics.firstLaunchCurrentVersionName)
        assertEquals(analytics.firstLaunchEver, analytics.firstLaunchCurrentVersion)
    }

    @Test
    fun recordAppLaunch_incrementLaunchCountOnSubsequentLaunches() {
        val analytics =
            AccountAnalytics(
                ownerId = testLocalAccountId(),
                firstLaunchEver = null,
                firstLaunchEverVersionName = null,
                currentVersion = null,
                firstLaunchCurrentVersion = null,
                firstLaunchCurrentVersionName = null,
                launchCount = 0,
            )

        val clock = Clock.System
        analytics.recordAppLaunch(versionName = "1.0.0", clock = clock)
        analytics.recordAppLaunch(versionName = "1.0.0", clock = clock)
        analytics.recordAppLaunch(versionName = "1.0.0", clock = clock)
        assertEquals(3, analytics.launchCount)
    }

    @Test
    fun recordAppLaunch_updateVersionInfoOnVersionChange() {
        val analytics =
            AccountAnalytics(
                ownerId = testLocalAccountId(),
                firstLaunchEver = Instant.DISTANT_PAST,
                firstLaunchEverVersionName = "1.0.0",
                currentVersion = "1.0.0",
                firstLaunchCurrentVersion = Instant.DISTANT_PAST,
                firstLaunchCurrentVersionName = "1.0.0",
                launchCount = 1,
            )

        analytics.recordAppLaunch(versionName = "2.0.0", clock = Clock.System)
        assertEquals(2, analytics.launchCount)
        assertEquals("1.0.0", analytics.firstLaunchEverVersionName)
        assertEquals("2.0.0", analytics.currentVersion)
        assertEquals("2.0.0", analytics.firstLaunchCurrentVersionName)
        assertTrue { analytics.firstLaunchCurrentVersion != analytics.firstLaunchEver }
    }
}
