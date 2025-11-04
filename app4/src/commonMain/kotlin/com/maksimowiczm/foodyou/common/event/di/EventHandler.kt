package com.maksimowiczm.foodyou.common.event.di

import com.maksimowiczm.foodyou.common.di.applicationCoroutineScope
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.common.event.IntegrationEvent
import com.maksimowiczm.foodyou.common.event.subscribe
import kotlin.jvm.JvmName
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.onClose

@JvmName("DomainEventHandler")
inline fun <reified E : DomainEvent> Module.eventHandler(
    qualifier: Qualifier = qualifier(E::class.qualifiedName!!),
    noinline definition: Scope.(ParametersHolder) -> EventHandler<E>,
) {
    single(qualifier = qualifier, createdAtStart = true) {
            get<EventBus<E>>(qualifier(DomainEvent::class.qualifiedName!!))
                .subscribe<E>(applicationCoroutineScope(), definition(it))
        }
        .onClose { it?.cancel() }
}

@JvmName("IntegrationEventHandler")
inline fun <reified E : IntegrationEvent> Module.eventHandler(
    qualifier: Qualifier = qualifier(E::class.qualifiedName!!),
    noinline definition: Scope.(ParametersHolder) -> EventHandler<E>,
) {
    single(qualifier = qualifier, createdAtStart = true) {
            get<EventBus<E>>(qualifier(IntegrationEvent::class.qualifiedName!!))
                .subscribe<E>(applicationCoroutineScope(), definition(it))
        }
        .onClose { it?.cancel() }
}
