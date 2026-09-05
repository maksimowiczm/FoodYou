@file:MustUseReturnValues

package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.Decider
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

fun Analytics.decide(command: AnalyticsCommand): List<AnalyticsEvent> =
    when (command) {
        is AnalyticsCommand.RecordAppLaunch ->
            buildList {
                add(
                    AppLaunchedEvent(
                        versionName = command.versionName,
                        timestamp = command.timestamp,
                    )
                )

                if (appFirstLaunchAt == null) {
                    add(
                        FirstAppLaunchRecordedEvent(
                            versionName = command.versionName,
                            timestamp = command.timestamp,
                        )
                    )
                }

                if (currentVersion != command.versionName) {
                    add(
                        AppVersionChangedEvent(
                            newVersionName = command.versionName,
                            timestamp = command.timestamp,
                        )
                    )
                }
            }
    }

/** Applies an [AnalyticsEvent] and returns a new immutable [Analytics] instance. */
fun Analytics.apply(event: AnalyticsEvent): Analytics =
    when (event) {
        is AppLaunchedEvent -> copy(launchCount = launchCount + 1)
        is FirstAppLaunchRecordedEvent ->
            copy(appFirstLaunchAt = event.timestamp, appFirstLaunchVersion = event.versionName)
        is AppVersionChangedEvent ->
            copy(
                currentVersion = event.newVersionName,
                currentVersionFirstLaunchAt = event.timestamp,
            )
    }

fun Iterable<AnalyticsEvent>.toAnalytics(): Analytics =
    fold(Analytics()) { state, event -> state.apply(event) }

val analyticsDecider =
    Decider<AnalyticsCommand, AnalyticsEvent, Analytics>(
        decide = { command, state -> state.decide(command) },
        evolve = { state, event -> state.apply(event) },
        initialState = Analytics(),
    )
