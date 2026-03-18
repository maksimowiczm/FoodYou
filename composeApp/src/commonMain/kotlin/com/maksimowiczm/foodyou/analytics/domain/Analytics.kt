package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.domain.AggregateRoot
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Clock
import kotlin.time.Instant

class Analytics : AggregateRoot() {
    var firstLaunchEver: Instant? = null
        private set

    var firstLaunchEverVersionName: String? = null
        private set

    var currentVersion: String? = null
        private set

    var firstLaunchCurrentVersion: Instant? = null
        private set

    var firstLaunchCurrentVersionName: String? = null
        private set

    var launchCount: Int = 0
        private set

    fun recordAppLaunch(versionName: String, clock: Clock) {
        val now = clock.now()

        val localEvents = buildList {
            add(AppLaunchedEvent(versionName = versionName, timestamp = now))

            if (firstLaunchEver == null) {
                add(FirstAppLaunchRecordedEvent(versionName = versionName, timestamp = now))
            }

            if (currentVersion != versionName) {
                add(AppVersionChangedEvent(newVersionName = versionName, timestamp = now))
            }
        }

        localEvents.forEach {
            apply(it)
            raise(it)
        }
    }

    fun apply(event: DomainEvent) =
        when (event) {
            is FirstAppLaunchRecordedEvent -> apply(event)
            is AppVersionChangedEvent -> apply(event)
            is AppLaunchedEvent -> apply(event)
            else -> Unit
        }

    private fun apply(event: FirstAppLaunchRecordedEvent) {
        firstLaunchEver = event.timestamp
        firstLaunchEverVersionName = event.versionName
    }

    private fun apply(event: AppVersionChangedEvent) {
        currentVersion = event.newVersionName
        firstLaunchCurrentVersion = event.timestamp
        firstLaunchCurrentVersionName = event.newVersionName
    }

    @Suppress("unused")
    private fun apply(event: AppLaunchedEvent) {
        launchCount++
    }
}
