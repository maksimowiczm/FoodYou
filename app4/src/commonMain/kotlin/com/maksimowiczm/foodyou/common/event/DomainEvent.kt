package com.maksimowiczm.foodyou.common.event

import kotlin.time.Instant

/**
 * Base interface for all domain events in the system.
 *
 * Domain events represent significant occurrences within the business domain. Each event is
 * immutable and captures the state change that occurred at a specific point in time.
 */
interface DomainEvent {
    /** The ID of the aggregate that the event is associated with. */
    val aggregateId: String

    /** The timestamp when the event was created. */
    val timestamp: Instant
}
