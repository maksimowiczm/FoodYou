package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

/**
 * Domain event emitted when a user launches the application for the first time.
 *
 * @property accountOwnerId Unique identifier of the account owner who launched the app
 * @property versionName Application version at first launch (e.g., "2.1.0")
 * @property timestamp When the first launch occurred
 */
@Serializable
data class FirstAppLaunchRecordedEvent(
    val accountOwnerId: String,
    val versionName: String,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : DomainEvent {
    override val aggregateId: String = accountOwnerId
}
