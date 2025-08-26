package com.maksimowiczm.foodyou.business.shared.application.event

import com.maksimowiczm.foodyou.business.shared.domain.event.DomainEvent

fun interface EventHandler<E : DomainEvent> {
    suspend fun handle(event: E)
}
