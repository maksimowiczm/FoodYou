package com.maksimowiczm.foodyou.settings.domain.event

import com.maksimowiczm.foodyou.common.domain.event.IntegrationEvent
import kotlin.time.Instant

data class AppLaunchEvent(val timestamp: Instant) : IntegrationEvent
