package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.LocalAccountId
import kotlin.time.Clock
import kotlin.time.Instant

class AccountAnalytics(
    val ownerId: LocalAccountId,
    firstLaunchEver: Instant?,
    firstLaunchEverVersionName: String?,
    currentVersion: String?,
    firstLaunchCurrentVersion: Instant?,
    firstLaunchCurrentVersionName: String?,
    launchCount: Int,
) {
    var firstLaunchEver: Instant? = firstLaunchEver
        private set

    var firstLaunchEverVersionName: String? = firstLaunchEverVersionName
        private set

    var currentVersion: String? = currentVersion
        private set

    var firstLaunchCurrentVersion: Instant? = firstLaunchCurrentVersion
        private set

    var firstLaunchCurrentVersionName: String? = firstLaunchCurrentVersionName
        private set

    var launchCount: Int = launchCount
        private set

    fun recordAppLaunch(versionName: String, clock: Clock) {
        val now = clock.now()

        if (firstLaunchEver == null) {
            firstLaunchEver = now
            firstLaunchEverVersionName = versionName
        }

        if (currentVersion != versionName) {
            currentVersion = versionName
            firstLaunchCurrentVersion = now
            firstLaunchCurrentVersionName = versionName
        }

        launchCount++
    }
}
