package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.domain.AggregateRoot
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Clock
import kotlin.time.Instant

class AccountAnalytics private constructor(val ownerId: LocalAccountId) : AggregateRoot() {
    companion object {
        fun of(ownerId: LocalAccountId): AccountAnalytics {
            return AccountAnalytics(ownerId = ownerId)
        }
    }

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
            add(
                AppLaunchedEvent(
                    accountOwnerId = ownerId.value,
                    versionName = versionName,
                    timestamp = now,
                )
            )

            if (firstLaunchEver == null) {
                add(
                    FirstAppLaunchRecordedEvent(
                        accountOwnerId = ownerId.value,
                        versionName = versionName,
                        timestamp = now,
                    )
                )
            }

            if (currentVersion != versionName) {
                add(
                    AppVersionChangedEvent(
                        accountOwnerId = ownerId.value,
                        newVersionName = versionName,
                        timestamp = now,
                    )
                )
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
