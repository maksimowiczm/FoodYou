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

inline fun <reified E : Event> Module.eventHandler(
    qualifier: Qualifier = named(E::class.qualifiedName!!),
    noinline definition: Scope.(ParametersHolder) -> EventHandler<E>,
) {
    factory(qualifier, definition)

    single(createdAtStart = true) {
            get<EventBus>()
                .subscribe<E>(
                    coroutineScope = applicationCoroutineScope(),
                    eventHandler = get(qualifier),
                )
        }
        .onClose { it?.cancel() }
}

inline fun <reified H : EventHandler<E>, reified E : Event> Module.eventHandlerOf(
    crossinline constructor: () -> H,
    qualifier: Qualifier = named(E::class.qualifiedName!!),
) {
    eventHandler(qualifier) { new(constructor) }
}

inline fun <reified H : EventHandler<E>, reified E : Event, reified T1> Module.eventHandlerOf(
    crossinline constructor: (T1) -> H,
    qualifier: Qualifier = named(E::class.qualifiedName!!),
) {
    eventHandler(qualifier) { new(constructor) }
}
