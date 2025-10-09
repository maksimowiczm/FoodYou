package com.maksimowiczm.foodyou.common.event

import kotlin.time.Instant

interface DomainEvent {
    /** The ID of the aggregate that the event is associated with. */
    val aggregateId: String

    /** The timestamp when the event was created. */
    val timestamp: Instant
}
