package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.Event
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.EventBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.subscribe
import com.maksimowiczm.foodyou.shared.common.infrastructure.event.EventHandler
import com.maksimowiczm.foodyou.shared.common.infrastructure.event.InMemoryEventBus
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.module.Module
import org.koin.core.module.dsl.new
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.onClose

const val EVENT_COROUTINE_SCOPE_NAME = "EventCS"

internal fun Module.eventBus() {
    single(named(EVENT_COROUTINE_SCOPE_NAME)) {
            CoroutineScope(
                Dispatchers.Default + SupervisorJob() + CoroutineName(EVENT_COROUTINE_SCOPE_NAME)
            )
        }
        .onClose { it?.cancel() }

    single<EventBus> { InMemoryEventBus() }
}

inline fun <reified E : Event> Module.eventHandler(
    qualifier: Qualifier = named(E::class.qualifiedName!!),
    noinline definition: Scope.(ParametersHolder) -> EventHandler<E>,
) {
    single(qualifier, true, definition)

    single(createdAtStart = true) {
            get<EventBus>()
                .subscribe<E>(
                    coroutineScope = get(named(EVENT_COROUTINE_SCOPE_NAME)),
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
