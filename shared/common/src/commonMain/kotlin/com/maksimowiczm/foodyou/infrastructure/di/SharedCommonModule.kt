package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import com.maksimowiczm.foodyou.shared.common.infrastructure.command.KoinCommandBus
import com.maksimowiczm.foodyou.shared.common.infrastructure.date.DateProviderImpl
import com.maksimowiczm.foodyou.shared.common.infrastructure.query.KoinQueryBus
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val systemDetails: Module.() -> Unit

val sharedCommonModule = module {
    singleOf(::KoinCommandBus).bind<CommandBus>()
    singleOf(::KoinQueryBus).bind<QueryBus>()

    eventBus()

    singleOf(::DateProviderImpl).bind<DateProvider>()

    systemDetails()
}
