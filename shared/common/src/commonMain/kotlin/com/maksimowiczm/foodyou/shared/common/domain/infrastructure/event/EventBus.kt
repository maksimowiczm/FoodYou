package com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event

interface EventBus {
    suspend fun <E : Event> publish(event: E)
}
