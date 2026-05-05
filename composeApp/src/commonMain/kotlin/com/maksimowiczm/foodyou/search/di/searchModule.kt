package com.maksimowiczm.foodyou.search.di

import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import com.maksimowiczm.foodyou.search.application.UserProductSearchSynchronizer
import com.maksimowiczm.foodyou.search.domain.SearchQueryParser
import com.maksimowiczm.foodyou.search.domain.SearchRepository
import com.maksimowiczm.foodyou.search.domain.history.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.search.domain.preferences.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.search.infrastructure.FoodSearchPreferencesRepositoryImpl
import com.maksimowiczm.foodyou.search.infrastructure.SearchRepositoryImpl
import com.maksimowiczm.foodyou.search.infrastructure.history.FoodSearchHistoryRepositoryImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val searchModule = module {
    factoryOf(::SearchRepositoryImpl).bind<SearchRepository>()
    eventHandlerOf(::UserProductSearchSynchronizer)

    factoryOf(::SearchQueryParser)

    factoryOf(::FoodSearchPreferencesRepositoryImpl).bind<FoodSearchPreferencesRepository>()

    factoryOf(::FoodSearchHistoryRepositoryImpl).bind<FoodSearchHistoryRepository>()
}
