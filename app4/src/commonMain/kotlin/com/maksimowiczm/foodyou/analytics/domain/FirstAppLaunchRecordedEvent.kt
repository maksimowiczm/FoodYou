package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant

data class FirstAppLaunchRecordedEvent(
    override val aggregateId: String,
    val versionName: String,
    val timestamp: Instant,
) : DomainEvent
