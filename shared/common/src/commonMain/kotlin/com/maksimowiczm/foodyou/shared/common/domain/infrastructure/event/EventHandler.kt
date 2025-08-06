package com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event

import kotlin.reflect.KClass

interface EventHandler<in E : Event> {
    val eventType: KClass<@UnsafeVariance E>

    suspend fun handle(event: E)
}
