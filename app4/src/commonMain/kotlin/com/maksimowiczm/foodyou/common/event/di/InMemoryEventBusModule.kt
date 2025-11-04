package com.maksimowiczm.foodyou.common.event.di

import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.InMemoryEventBus
import com.maksimowiczm.foodyou.common.event.IntegrationEvent
import org.koin.core.qualifier.qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module

val inMemoryEventBusModule = module {
    single(qualifier(DomainEvent::class.qualifiedName!!)) { InMemoryEventBus<DomainEvent>() }
        .bind<EventBus<DomainEvent>>()

    single(qualifier(IntegrationEvent::class.qualifiedName!!)) {
            InMemoryEventBus<IntegrationEvent>()
        }
        .bind<EventBus<IntegrationEvent>>()
}

fun Scope.domainEventBus() =
    get(qualifier(DomainEvent::class.qualifiedName!!)) as EventBus<DomainEvent>

fun Scope.integrationEventBus() =
    get(qualifier(IntegrationEvent::class.qualifiedName!!)) as EventBus<IntegrationEvent>
