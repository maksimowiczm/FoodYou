package com.maksimowiczm.foodyou.common.event

interface EventHandler<E> {
    suspend fun handle(event: E)
}
