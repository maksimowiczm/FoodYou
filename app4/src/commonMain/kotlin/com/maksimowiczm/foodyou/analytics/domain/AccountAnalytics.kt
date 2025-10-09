package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.AggregateRoot
import com.maksimowiczm.foodyou.common.EventSourcedAggregateRoot
import com.maksimowiczm.foodyou.common.LocalAccountId
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Clock
import kotlin.time.Instant

class AccountAnalytics private constructor(val ownerId: LocalAccountId) :
    AggregateRoot(), EventSourcedAggregateRoot {
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
        val localEvents = mutableListOf<DomainEvent>()

        localEvents.add(
            AppLaunchedEvent(
                aggregateId = ownerId.value,
                versionName = versionName,
                timestamp = now,
            )
        )

        if (firstLaunchEver == null) {
            localEvents.add(
                FirstAppLaunchRecordedEvent(
                    aggregateId = ownerId.value,
                    versionName = versionName,
                    timestamp = now,
                )
            )
        }

        if (currentVersion != versionName) {
            localEvents.add(
                AppVersionChangedEvent(
                    aggregateId = ownerId.value,
                    newVersionName = versionName,
                    timestamp = now,
                )
            )
        }

        localEvents.forEach {
            apply(it)
            raise(it)
        }
    }

    override fun apply(event: DomainEvent) {
        when (event) {
            is FirstAppLaunchRecordedEvent -> {
                firstLaunchEver = event.timestamp
                firstLaunchEverVersionName = event.versionName
            }

            is AppVersionChangedEvent -> {
                currentVersion = event.newVersionName
                firstLaunchCurrentVersion = event.timestamp
                firstLaunchCurrentVersionName = event.newVersionName
            }

            is AppLaunchedEvent -> {
                launchCount++
            }

            else -> error("Unknown event type: ${event::class.simpleName}")
        }
    }
}
