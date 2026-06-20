package com.maksimowiczm.foodyou.search.di

import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import com.maksimowiczm.foodyou.search.application.SearchHistoryService
import com.maksimowiczm.foodyou.search.application.UserProductSearchSynchronizer
import com.maksimowiczm.foodyou.search.application.UserRecipeSearchSynchronizer
import com.maksimowiczm.foodyou.search.domain.SearchRepository
import com.maksimowiczm.foodyou.search.infrastructure.SearchDatabase
import com.maksimowiczm.foodyou.search.infrastructure.SearchRepositoryImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val searchModule = module {
    factoryOf(::SearchRepositoryImpl).bind<SearchRepository>()
    eventHandlerOf(::UserProductSearchSynchronizer)
    eventHandlerOf(::UserRecipeSearchSynchronizer)

    single { get<SearchDatabase>().searchDao }

    factoryOf(::SearchHistoryService)
}
