package com.maksimowiczm.foodyou.common.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent

interface EventSourcedAggregateRoot {
    fun apply(event: DomainEvent)
}
