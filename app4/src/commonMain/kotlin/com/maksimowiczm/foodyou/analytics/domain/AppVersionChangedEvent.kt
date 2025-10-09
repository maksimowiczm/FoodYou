package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant

data class AppVersionChangedEvent(
    override val aggregateId: String,
    val newVersionName: String,
    val timestamp: Instant,
) : DomainEvent
