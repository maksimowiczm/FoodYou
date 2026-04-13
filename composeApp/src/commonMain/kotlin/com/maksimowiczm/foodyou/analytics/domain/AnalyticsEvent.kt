package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlinx.serialization.Serializable

@Serializable sealed interface AnalyticsEvent : DomainEvent
