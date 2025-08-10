package com.maksimowiczm.foodyou.shared.common.infrastructure.event

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.Event

fun interface EventHandler<E : Event> {
    suspend fun handle(event: E)
}
