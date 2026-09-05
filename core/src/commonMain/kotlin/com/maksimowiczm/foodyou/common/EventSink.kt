package com.maksimowiczm.foodyou.common

import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.common.event.EventBus

fun interface EventSink {
    suspend fun publish(events: List<DomainEvent>)
}

fun EventBus.asEventSink() = EventSink { publish(it) }
