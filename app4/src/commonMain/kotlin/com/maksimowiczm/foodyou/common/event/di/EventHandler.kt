package com.maksimowiczm.foodyou.common.event.di

import com.maksimowiczm.foodyou.common.di.applicationCoroutineScope
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.common.event.IntegrationEvent
import com.maksimowiczm.foodyou.common.event.subscribe
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.onClose

/**
 * Registers an event handler as a Koin singleton that subscribes to events from the specified event
 * bus.
 *
 * This function creates a singleton that:
 * - Retrieves the event bus using the provided qualifier
 * - Subscribes the event handler to the event bus using the application coroutine scope
 * - Automatically cancels the subscription when the Koin container closes
 *
 * @param E The type of event this handler processes
 * @param eventBusQualifier The qualifier used to retrieve the appropriate EventBus instance
 * @param handlerQualifier The qualifier for this event handler singleton
 * @param definition A factory function that creates the EventHandler instance
 * @see EventBus
 * @see EventHandler
 * @see domainEventHandler
 * @see integrationEventHandler
 */
inline fun <reified E> Module.eventHandler(
    eventBusQualifier: Qualifier,
    handlerQualifier: Qualifier,
    noinline definition: Scope.(ParametersHolder) -> EventHandler<E>,
) {
    single(qualifier = handlerQualifier, createdAtStart = true) {
            get<EventBus<E>>(eventBusQualifier)
                .subscribe<E>(applicationCoroutineScope(), definition(it))
        }
        .onClose { it?.cancel() }
}

/**
 * Registers a domain event handler in the Koin module.
 *
 * This is a convenience function that simplifies the registration of handlers for [DomainEvent]s.
 * It automatically uses the domain event bus and generates a qualifier based on the handler's
 * qualified name if none is provided.
 *
 * @param E The specific type of DomainEvent this handler processes
 * @param H The EventHandler implementation type
 * @param qualifier Optional qualifier for this handler. Defaults to the handler's qualified class
 *   name
 * @param definition A factory function that creates the EventHandler instance
 * @see DomainEvent
 * @see EventHandler
 * @see eventHandler
 * @see integrationEventHandler
 */
inline fun <reified E : DomainEvent, reified H : EventHandler<E>> Module.domainEventHandler(
    qualifier: Qualifier = qualifier(H::class.qualifiedName!!),
    noinline definition: Scope.(ParametersHolder) -> H,
) {
    eventHandler(qualifier(DomainEvent::class.qualifiedName!!), qualifier, definition)
}

/**
 * Registers an integration event handler in the Koin module.
 *
 * This is a convenience function that simplifies the registration of handlers for
 * [IntegrationEvent]s. It automatically uses the integration event bus and generates a qualifier
 * based on the handler's qualified name if none is provided.
 *
 * @param E The specific type of IntegrationEvent this handler processes
 * @param H The EventHandler implementation type
 * @param qualifier Optional qualifier for this handler. Defaults to the handler's qualified class
 *   name
 * @param definition A factory function that creates the EventHandler instance
 * @see IntegrationEvent
 * @see EventHandler
 * @see eventHandler
 * @see domainEventHandler
 */
inline fun <reified E : IntegrationEvent, reified H : EventHandler<E>> Module
    .integrationEventHandler(
    qualifier: Qualifier = qualifier(H::class.qualifiedName!!),
    noinline definition: Scope.(ParametersHolder) -> H,
) {
    eventHandler(qualifier(IntegrationEvent::class.qualifiedName!!), qualifier, definition)
}
