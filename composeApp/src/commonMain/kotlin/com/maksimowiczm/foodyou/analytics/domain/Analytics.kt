@file:MustUseReturnValues

package com.maksimowiczm.foodyou.analytics.domain

import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Analytics projection rebuilt from [AnalyticsEvent]s.
 *
 * This model is a pure value object. It contains no behavior that mutates state — instead each
 * [apply] call returns a **new instance** with updated values.
 *
 * @property appFirstLaunchAt Timestamp of the first launch ever, or `null` if not recorded.
 * @property appFirstLaunchVersion Version of the first launch ever.
 * @property currentVersion Most recently launched version.
 * @property currentVersionFirstLaunchAt First launch timestamp of the current version.
 * @property launchCount Total number of launches.
 */
data class Analytics(
    val appFirstLaunchAt: Instant? = null,
    val appFirstLaunchVersion: String? = null,
    val currentVersion: String? = null,
    val currentVersionFirstLaunchAt: Instant? = null,
    val launchCount: Int = 0,
)

/**
 * Records an application launch
 *
 * @param versionName The version name of the application at the time of launch (e.g. `"1.4.2"`).
 * @param clock A [Clock] used to obtain the current timestamp.
 */
fun Analytics.recordAppLaunch(versionName: String, clock: Clock) = buildList {
    val now = clock.now()

    add(AppLaunchedEvent(versionName = versionName, timestamp = now))

    if (appFirstLaunchAt == null) {
        add(FirstAppLaunchRecordedEvent(versionName = versionName, timestamp = now))
    }

    if (currentVersion != versionName) {
        add(AppVersionChangedEvent(newVersionName = versionName, timestamp = now))
    }
}

/** Applies an [AnalyticsEvent] and returns a new immutable [Analytics] instance. */
fun Analytics.apply(event: AnalyticsEvent): Analytics =
    when (event) {
        is AppLaunchedEvent -> apply(event)
        is FirstAppLaunchRecordedEvent -> apply(event)
        is AppVersionChangedEvent -> apply(event)
    }

@Suppress("unused")
fun Analytics.apply(event: AppLaunchedEvent): Analytics = copy(launchCount = launchCount + 1)

fun Analytics.apply(event: FirstAppLaunchRecordedEvent): Analytics =
    copy(appFirstLaunchAt = event.timestamp, appFirstLaunchVersion = event.versionName)

fun Analytics.apply(event: AppVersionChangedEvent): Analytics =
    copy(currentVersion = event.newVersionName, currentVersionFirstLaunchAt = event.timestamp)

fun Iterable<AnalyticsEvent>.toAnalytics(): Analytics =
    fold(Analytics()) { state, event -> state.apply(event) }
