package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.Event
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.EventBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.subscribe
import com.maksimowiczm.foodyou.shared.common.infrastructure.event.EventHandler
import org.koin.core.module.Module
import org.koin.core.module.dsl.new
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.onClose

inline fun <reified H : EventHandler<E>, reified E : Event> Module.eventHandler(
    eventQualifier: Qualifier = named(E::class.qualifiedName!!),
    handlerQualifier: Qualifier = named(H::class.qualifiedName!!),
    noinline definition: Scope.(ParametersHolder) -> EventHandler<E>,
) {
    factory(eventQualifier, definition)

    single(qualifier = handlerQualifier, createdAtStart = true) {
            get<EventBus>()
                .subscribe<E>(
                    coroutineScope = applicationCoroutineScope(),
                    eventHandler = get(eventQualifier),
                )
        }
        .onClose { it?.cancel() }
}

inline fun <reified H : EventHandler<E>, reified E : Event> Module.eventHandlerOf(
    crossinline constructor: () -> H,
    qualifier: Qualifier = named(E::class.qualifiedName!!),
    handlerQualifier: Qualifier = named(H::class.qualifiedName!!),
) {
    eventHandler(qualifier, handlerQualifier) { new(constructor) }
}

inline fun <reified H : EventHandler<E>, reified E : Event, reified T1> Module.eventHandlerOf(
    crossinline constructor: (T1) -> H,
    qualifier: Qualifier = named(E::class.qualifiedName!!),
    handlerQualifier: Qualifier = named(H::class.qualifiedName!!),
) {
    eventHandler(qualifier, handlerQualifier) { new(constructor) }
}
