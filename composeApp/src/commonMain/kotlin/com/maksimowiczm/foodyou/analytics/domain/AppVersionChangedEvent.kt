package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

/**
 * Domain event emitted when a user updates to a new application version.
 *
 * @property accountOwnerId Unique identifier of the account owner who updated the app
 * @property newVersionName The new application version (e.g., "2.1.0")
 * @property timestamp When the version change was detected
 */
@Serializable
data class AppVersionChangedEvent(
    val accountOwnerId: String,
    val newVersionName: String,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : DomainEvent {
    override val aggregateId: String = accountOwnerId
}
