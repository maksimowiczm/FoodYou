package com.maksimowiczm.foodyou.business.settings.domain

import com.maksimowiczm.foodyou.shared.domain.event.DomainEvent
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class) data class AppLaunchEvent(val timestamp: Instant) : DomainEvent
