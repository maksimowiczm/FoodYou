package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed interface AnalyticsEvent : DomainEvent

/**
 * Domain event emitted when the application is launched.
 *
 * @property versionName Application version at launch time (e.g., "2.1.0")
 */
@Serializable
data class AppLaunchedEvent(
    val versionName: String,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : AnalyticsEvent

/**
 * Domain event emitted when a user updates to a new application version.
 *
 * @property newVersionName The new application version (e.g., "2.1.0")
 */
@Serializable
data class AppVersionChangedEvent(
    val newVersionName: String,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : AnalyticsEvent

/**
 * Domain event emitted when a user launches the application for the first time.
 *
 * @property versionName Application version at first launch (e.g., "2.1.0")
 */
@Serializable
data class FirstAppLaunchRecordedEvent(
    val versionName: String,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : AnalyticsEvent
