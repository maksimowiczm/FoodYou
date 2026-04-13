package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.domain.AggregateRoot
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Aggregate root that tracks application launch analytics.
 *
 * This class models the analytics state of the application using event sourcing. State is never
 * mutated directly — all changes flow through domain events. Use [of] to create a fresh instance or
 * [replayFrom] to restore state from a persisted event history.
 *
 * @see AnalyticsEvent
 * @see AggregateRoot
 */
class Analytics private constructor() : AggregateRoot<AnalyticsEvent>() {

    companion object {

        /**
         * Creates a new [Analytics] instance with no recorded events.
         *
         * Use this when initializing analytics for the first time (i.e. no persisted event history
         * exists yet).
         */
        fun of() = Analytics()

        /**
         * Reconstructs an [Analytics] instance by replaying a sequence of previously persisted
         * events.
         *
         * Events are applied in the order they are provided. The resulting state is identical to
         * what it would have been had those events been raised live via [recordAppLaunch].
         *
         * @param events Ordered list of [AnalyticsEvent]s to replay.
         * @return An [Analytics] instance whose state reflects all replayed events.
         */
        fun replayFrom(events: List<AnalyticsEvent>): Analytics {
            val analytics = Analytics()
            events.forEach(analytics::apply)
            return analytics
        }
    }

    /**
     * The timestamp of the very first time the application was ever launched, or `null` if no
     * launch has been recorded yet.
     */
    var firstLaunchEver: Instant? = null
        private set

    /**
     * The version name of the application at the time of its very first launch, or `null` if no
     * launch has been recorded yet.
     */
    var firstLaunchEverVersionName: String? = null
        private set

    /**
     * The version name of the most recently recorded application launch, or `null` if no launch has
     * been recorded yet.
     */
    var currentVersion: String? = null
        private set

    /** The timestamp of the first launch ever, or `null` if no launch has been recorded yet. */
    var firstLaunchCurrentVersion: Instant? = null
        private set

    /**
     * The version name associated with [firstLaunchCurrentVersion].
     *
     * `null` if no launch has been recorded yet.
     */
    var firstLaunchCurrentVersionName: String? = null
        private set

    /**
     * The total number of times the application has been launched since analytics tracking began.
     */
    var launchCount: Int = 0
        private set

    /**
     * Records an application launch
     *
     * @param versionName The version name of the application at the time of launch (e.g.
     *   `"1.4.2"`).
     * @param clock A [Clock] used to obtain the current timestamp.
     */
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

        localEvents.forEach { raise(it) }
    }

    override fun apply(event: AnalyticsEvent) =
        when (event) {
            is FirstAppLaunchRecordedEvent -> apply(event)
            is AppVersionChangedEvent -> apply(event)
            is AppLaunchedEvent -> apply(event)
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
