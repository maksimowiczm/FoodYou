package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.DefinitionOptions
import org.koin.core.module.dsl.new
import org.koin.core.module.dsl.onOptions
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named

inline fun <reified H : CommandHandler<C, *, *>, reified C : Command<*, *>> Module.commandHandler(
    qualifier: Qualifier = named(C::class.qualifiedName!!),
    noinline definition: Definition<H>,
): KoinDefinition<CommandHandler<*, *, *>> = factory(qualifier, definition)

inline fun <reified H : CommandHandler<C, *, *>, reified C : Command<*, *>, reified T1> Module
    .commandHandlerOf(
    crossinline constructor: (T1) -> H,
    qualifier: Qualifier = named(C::class.qualifiedName!!),
    noinline options: DefinitionOptions<CommandHandler<*, *, *>>? = null,
): KoinDefinition<CommandHandler<*, *, *>> =
    commandHandler(qualifier) { new(constructor) }.onOptions(options)

inline fun <
    reified H : CommandHandler<C, *, *>,
    reified C : Command<*, *>,
    reified T1,
    reified T2,
> Module.commandHandlerOf(
    crossinline constructor: (T1, T2) -> H,
    qualifier: Qualifier = named(C::class.qualifiedName!!),
    noinline options: DefinitionOptions<CommandHandler<*, *, *>>? = null,
): KoinDefinition<CommandHandler<*, *, *>> =
    commandHandler(qualifier) { new(constructor) }.onOptions(options)

inline fun <
    reified H : CommandHandler<C, *, *>,
    reified C : Command<*, *>,
    reified T1,
    reified T2,
    reified T3,
> Module.commandHandlerOf(
    crossinline constructor: (T1, T2, T3) -> H,
    qualifier: Qualifier = named(C::class.qualifiedName!!),
    noinline options: DefinitionOptions<CommandHandler<*, *, *>>? = null,
): KoinDefinition<CommandHandler<*, *, *>> =
    commandHandler(qualifier) { new(constructor) }.onOptions(options)

inline fun <
    reified H : CommandHandler<C, *, *>,
    reified C : Command<*, *>,
    reified T1,
    reified T2,
    reified T3,
    reified T4,
> Module.commandHandlerOf(
    crossinline constructor: (T1, T2, T3, T4) -> H,
    qualifier: Qualifier = named(C::class.qualifiedName!!),
    noinline options: DefinitionOptions<CommandHandler<*, *, *>>? = null,
): KoinDefinition<CommandHandler<*, *, *>> =
    commandHandler(qualifier) { new(constructor) }.onOptions(options)
