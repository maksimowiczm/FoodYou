package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.DefinitionOptions
import org.koin.core.module.dsl.new
import org.koin.core.module.dsl.onOptions
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named

inline fun <reified H : QueryHandler<Q, *>, reified Q : Query<*>> Module.queryHandler(
    qualifier: Qualifier = named(Q::class.qualifiedName!!),
    noinline definition: Definition<H>,
): KoinDefinition<QueryHandler<*, *>> = factory(qualifier, definition)

inline fun <reified H : QueryHandler<Q, *>, reified Q : Query<*>> Module.queryHandlerOf(
    crossinline constructor: () -> H,
    qualifier: Qualifier = named(Q::class.qualifiedName!!),
    noinline options: DefinitionOptions<QueryHandler<*, *>>? = null,
): KoinDefinition<QueryHandler<*, *>> =
    queryHandler(qualifier) { new(constructor) }.onOptions(options)

inline fun <reified H : QueryHandler<Q, *>, reified Q : Query<*>, reified T1> Module.queryHandlerOf(
    crossinline constructor: (T1) -> H,
    qualifier: Qualifier = named(Q::class.qualifiedName!!),
    noinline options: DefinitionOptions<QueryHandler<*, *>>? = null,
): KoinDefinition<QueryHandler<*, *>> =
    queryHandler(qualifier) { new(constructor) }.onOptions(options)

inline fun <reified H : QueryHandler<Q, *>, reified Q : Query<*>, reified T1, reified T2> Module
    .queryHandlerOf(
    crossinline constructor: (T1, T2) -> H,
    qualifier: Qualifier = named(Q::class.qualifiedName!!),
    noinline options: DefinitionOptions<QueryHandler<*, *>>? = null,
): KoinDefinition<QueryHandler<*, *>> =
    queryHandler(qualifier) { new(constructor) }.onOptions(options)

inline fun <
    reified H : QueryHandler<Q, *>,
    reified Q : Query<*>,
    reified T1,
    reified T2,
    reified T3,
> Module.queryHandlerOf(
    crossinline constructor: (T1, T2, T3) -> H,
    qualifier: Qualifier = named(Q::class.qualifiedName!!),
    noinline options: DefinitionOptions<QueryHandler<*, *>>? = null,
): KoinDefinition<QueryHandler<*, *>> =
    queryHandler(qualifier) { new(constructor) }.onOptions(options)

inline fun <
    reified H : QueryHandler<Q, *>,
    reified Q : Query<*>,
    reified T1,
    reified T2,
    reified T3,
    reified T4,
> Module.queryHandlerOf(
    crossinline constructor: (T1, T2, T3, T4) -> H,
    qualifier: Qualifier = named(Q::class.qualifiedName!!),
    noinline options: DefinitionOptions<QueryHandler<*, *>>? = null,
): KoinDefinition<QueryHandler<*, *>> =
    queryHandler(qualifier) { new(constructor) }.onOptions(options)

inline fun <
    reified H : QueryHandler<Q, *>,
    reified Q : Query<*>,
    reified T1,
    reified T2,
    reified T3,
    reified T4,
    reified T5,
    reified T6,
    reified T7,
    reified T8,
    reified T9,
    reified T10,
    reified T11,
    reified T12,
    reified T13,
    reified T14,
> Module.queryHandlerOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> H,
    qualifier: Qualifier = named(Q::class.qualifiedName!!),
    noinline options: DefinitionOptions<QueryHandler<*, *>>? = null,
): KoinDefinition<QueryHandler<*, *>> =
    queryHandler(qualifier) { new(constructor) }.onOptions(options)
