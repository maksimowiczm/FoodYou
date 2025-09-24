package com.maksimowiczm.foodyou.common.domain.event

fun interface IntegrationEventHandler<E : IntegrationEvent> {
    suspend fun handle(event: E)
}
