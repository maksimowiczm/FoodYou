package com.maksimowiczm.foodyou.common

import com.maksimowiczm.foodyou.common.event.DomainEvent

interface EventSourcedAggregateRoot {
    fun apply(event: DomainEvent)
}
