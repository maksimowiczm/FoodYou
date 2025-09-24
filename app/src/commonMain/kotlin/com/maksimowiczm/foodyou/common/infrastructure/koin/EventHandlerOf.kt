package com.maksimowiczm.foodyou.common.infrastructure.koin

import com.maksimowiczm.foodyou.common.domain.event.EventBus
import com.maksimowiczm.foodyou.common.domain.event.IntegrationEvent
import com.maksimowiczm.foodyou.common.domain.event.IntegrationEventHandler
import com.maksimowiczm.foodyou.common.domain.event.subscribe
import org.koin.core.module.Module
import org.koin.core.module.dsl.new
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.onClose

inline fun <reified H : IntegrationEventHandler<E>, reified E : IntegrationEvent> Module
    .eventHandler(
    qualifier: Qualifier = named(H::class.qualifiedName!!),
    noinline definition: Scope.(ParametersHolder) -> IntegrationEventHandler<E>,
) {
    single(qualifier = qualifier, createdAtStart = true) {
            get<EventBus>()
                .subscribe<E>(
                    coroutineScope = applicationCoroutineScope(),
                    integrationEventHandler = definition(it),
                )
        }
        .onClose { it?.cancel() }
}

inline fun <reified H : IntegrationEventHandler<E>, reified E : IntegrationEvent> Module
    .eventHandlerOf(
    crossinline constructor: () -> H,
    qualifier: Qualifier = named(E::class.qualifiedName!!),
) {
    eventHandler(qualifier) { new(constructor) }
}

inline fun <reified H : IntegrationEventHandler<E>, reified E : IntegrationEvent, reified T1> Module
    .eventHandlerOf(
    crossinline constructor: (T1) -> H,
    qualifier: Qualifier = named(E::class.qualifiedName!!),
) {
    eventHandler(qualifier) { new(constructor) }
}

inline fun <
    reified H : IntegrationEventHandler<E>,
    reified E : IntegrationEvent,
    reified T1,
    reified T2,
> Module.eventHandlerOf(
    crossinline constructor: (T1, T2) -> H,
    qualifier: Qualifier = named(E::class.qualifiedName!!),
) {
    eventHandler(qualifier) { new(constructor) }
}
