package com.maksimowiczm.foodyou.common.event

import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * Base interface for all domain events in the system.
 *
 * Domain events represent significant occurrences within the business domain. Each event is
 * immutable and captures the state change that occurred at a specific point in time.
 */
interface DomainEvent {
    val id: Uuid
    val timestamp: Instant
}
