package com.maksimowiczm.foodyou.business.shared.di

import com.maksimowiczm.foodyou.shared.domain.event.DomainEvent
import com.maksimowiczm.foodyou.shared.domain.event.EventBus
import com.maksimowiczm.foodyou.shared.domain.event.EventHandler
import com.maksimowiczm.foodyou.shared.domain.event.subscribe
import org.koin.core.module.Module
import org.koin.core.module.dsl.new
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.onClose

inline fun <reified H : EventHandler<E>, reified E : DomainEvent> Module.eventHandler(
    qualifier: Qualifier = named(H::class.qualifiedName!!),
    noinline definition: Scope.(ParametersHolder) -> EventHandler<E>,
) {
    single(qualifier = qualifier, createdAtStart = true) {
            get<EventBus>()
                .subscribe<E>(
                    coroutineScope = applicationCoroutineScope(),
                    eventHandler = definition(it),
                )
        }
        .onClose { it?.cancel() }
}

inline fun <reified H : EventHandler<E>, reified E : DomainEvent> Module.eventHandlerOf(
    crossinline constructor: () -> H,
    qualifier: Qualifier = named(E::class.qualifiedName!!),
) {
    eventHandler(qualifier) { new(constructor) }
}

inline fun <reified H : EventHandler<E>, reified E : DomainEvent, reified T1> Module.eventHandlerOf(
    crossinline constructor: (T1) -> H,
    qualifier: Qualifier = named(E::class.qualifiedName!!),
) {
    eventHandler(qualifier) { new(constructor) }
}

inline fun <reified H : EventHandler<E>, reified E : DomainEvent, reified T1, reified T2> Module
    .eventHandlerOf(
    crossinline constructor: (T1, T2) -> H,
    qualifier: Qualifier = named(E::class.qualifiedName!!),
) {
    eventHandler(qualifier) { new(constructor) }
}
