package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.domain.event.DomainEvent
import kotlin.time.Instant

data class AppLaunchedEvent(val versionName: String, val timestamp: Instant) : DomainEvent
