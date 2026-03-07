package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

/**
 * Domain event emitted when the application is launched.
 *
 * @property accountOwnerId Unique identifier of the user or device that launched the app
 * @property versionName Application version at launch time (e.g., "2.1.0")
 * @property timestamp When the application was launched
 */
@Serializable
data class AppLaunchedEvent(
    val accountOwnerId: String,
    val versionName: String,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : DomainEvent {
    override val aggregateId: String = accountOwnerId
}
