package com.maksimowiczm.foodyou.common.event.di

import com.maksimowiczm.foodyou.common.di.applicationCoroutineScope
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.common.event.subscribe
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.onClose

inline fun <reified E : DomainEvent> Module.eventHandler(
    qualifier: Qualifier? = null,
    noinline definition: Scope.(ParametersHolder) -> EventHandler<E>,
) {
    single(qualifier = qualifier, createdAtStart = true) {
            get<EventBus>().subscribe<E>(applicationCoroutineScope(), definition(it))
        }
        .onClose { it?.cancel() }
}
