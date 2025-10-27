package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.account.domain.testLocalAccountId
import com.maksimowiczm.foodyou.common.clock.testClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant

class AccountAnalyticsTest {
    @Test
    fun recordAppLaunch_once() {
        val analytics = AccountAnalytics.of(testLocalAccountId())
        val versionName = "4.0.0"
        val now = Instant.fromEpochSeconds(1_600_000_000)
        val clock = testClock(now)

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
        val analytics = AccountAnalytics.of(testLocalAccountId())
        val versionNameV1 = "4.0.0"
        val versionNameV2 = "4.1.0"
        val now1 = Instant.fromEpochSeconds(1_600_000_000)
        val now2 = Instant.fromEpochSeconds(1_600_000_100)
        val clock1 = testClock(now1)
        val clock2 = testClock(now2)

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
        val analytics = AccountAnalytics.of(testLocalAccountId())
        val versionName = "4.0.0"
        val now = Instant.fromEpochSeconds(1_600_000_000)
        val clock = testClock(now)

        analytics.recordAppLaunch(versionName = versionName, clock = clock)

        val events = analytics.events
        assertEquals(3, events.size)
        assertIs<AppLaunchedEvent>(events[0])
        assertIs<FirstAppLaunchRecordedEvent>(events[1])
        assertIs<AppVersionChangedEvent>(events[2])
    }

    @Test
    fun applyAppLaunchedEvent() {
        val analytics = AccountAnalytics.of(testLocalAccountId())
        val versionName = "4.0.0"
        val now = Instant.fromEpochSeconds(1_600_000_000)
        val event =
            AppLaunchedEvent(
                aggregateId = "test-account",
                versionName = versionName,
                timestamp = now,
            )

        analytics.apply(event)

        assertEquals(1, analytics.launchCount)
    }

    @Test
    fun applyFirstAppLaunchRecordedEvent() {
        val analytics = AccountAnalytics.of(testLocalAccountId())
        val versionName = "4.0.0"
        val now = Instant.fromEpochSeconds(1_600_000_000)
        val event =
            FirstAppLaunchRecordedEvent(
                aggregateId = "test-account",
                versionName = versionName,
                timestamp = now,
            )

        analytics.apply(event)

        assertEquals(now, analytics.firstLaunchEver)
        assertEquals(versionName, analytics.firstLaunchEverVersionName)
    }

    @Test
    fun applyAppVersionChangedEvent() {
        val analytics = AccountAnalytics.of(testLocalAccountId())
        val versionName = "4.0.0"
        val now = Instant.fromEpochSeconds(1_600_000_000)
        val event =
            AppVersionChangedEvent(
                aggregateId = "test-account",
                newVersionName = versionName,
                timestamp = now,
            )

        analytics.apply(event)

        assertEquals(versionName, analytics.currentVersion)
        assertEquals(now, analytics.firstLaunchCurrentVersion)
        assertEquals(versionName, analytics.firstLaunchCurrentVersionName)
    }
}
