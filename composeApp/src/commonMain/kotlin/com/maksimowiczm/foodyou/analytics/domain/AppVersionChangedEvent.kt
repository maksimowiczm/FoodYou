package com.maksimowiczm.foodyou.analytics.domain

import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

/**
 * Domain event emitted when a user updates to a new application version.
 *
 * @property newVersionName The new application version (e.g., "2.1.0")
 * @property timestamp When the version change was detected
 */
@Serializable
data class AppVersionChangedEvent(
    val newVersionName: String,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : AnalyticsEvent
