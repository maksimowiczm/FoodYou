package com.maksimowiczm.foodyou.common.event.di

import com.maksimowiczm.foodyou.common.di.applicationCoroutineScope
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.common.event.EventNotifier
import com.maksimowiczm.foodyou.common.event.subscribe
import kotlinx.coroutines.Job
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.DefinitionOptions
import org.koin.core.module.dsl.new
import org.koin.core.module.dsl.onOptions
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
 * - Subscribes the event handler to the event bus using the application coroutine scope
 * - Automatically cancels the subscription when the Koin container closes
 *
 * @param E The type of event this handler processes
 * @param definition A factory function that creates the EventHandler instance
 * @see EventNotifier
 * @see EventHandler
 */
inline fun <reified E : DomainEvent, reified H : EventHandler<E>> Module.eventHandler(
    qualifier: Qualifier = qualifier(H::class.qualifiedName!!),
    noinline definition: Scope.(ParametersHolder) -> H,
): KoinDefinition<Job> =
    single(qualifier = qualifier, createdAtStart = true) {
            get<EventNotifier>().subscribe<E>(applicationCoroutineScope(), definition(it))
        }
        .onClose { it?.cancel() }

inline fun <reified E : DomainEvent, reified H : EventHandler<E>, reified T1> Module.eventHandlerOf(
    crossinline constructor: (T1) -> H,
    noinline options: DefinitionOptions<Job>? = null,
): KoinDefinition<Job> = eventHandler { new(constructor) }.onOptions(options)

inline fun <reified E : DomainEvent, reified H : EventHandler<E>, reified T1, reified T2> Module
    .eventHandlerOf(
    crossinline constructor: (T1, T2) -> H,
    noinline options: DefinitionOptions<Job>? = null,
): KoinDefinition<Job> = eventHandler { new(constructor) }.onOptions(options)
