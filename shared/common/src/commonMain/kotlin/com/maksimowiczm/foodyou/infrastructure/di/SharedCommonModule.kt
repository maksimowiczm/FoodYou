package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.EventBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import com.maksimowiczm.foodyou.shared.common.infrastructure.command.KoinCommandBus
import com.maksimowiczm.foodyou.shared.common.infrastructure.date.DateProviderImpl
import com.maksimowiczm.foodyou.shared.common.infrastructure.event.InMemoryEventBus
import com.maksimowiczm.foodyou.shared.common.infrastructure.query.KoinQueryBus
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module

expect val systemDetails: Module.() -> Unit

private const val APPLICATION_COROUTINE_SCOPE = "APPLICATION_COROUTINE_SCOPE"

fun Scope.applicationCoroutineScope(): CoroutineScope = get(named(APPLICATION_COROUTINE_SCOPE))

fun sharedCommonModule(coroutineScope: CoroutineScope): Module = module {
    single(named(APPLICATION_COROUTINE_SCOPE)) { coroutineScope }

    singleOf(::KoinCommandBus).bind<CommandBus>()
    singleOf(::KoinQueryBus).bind<QueryBus>()
    singleOf(::InMemoryEventBus).bind<EventBus>()

    singleOf(::DateProviderImpl).bind<DateProvider>()

    systemDetails()
}
