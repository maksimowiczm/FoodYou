package com.maksimowiczm.foodyou.business.shared.application.event

import com.maksimowiczm.foodyou.business.shared.domain.event.DomainEvent
import kotlinx.coroutines.flow.Flow

interface EventBus {

    val events: Flow<DomainEvent>

    fun publish(domainEvent: DomainEvent)
}
