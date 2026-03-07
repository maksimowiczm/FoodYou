package com.maksimowiczm.foodyou.foodsearch.di

import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQueryParser
import com.maksimowiczm.foodyou.foodsearch.infrastructure.FoodSearchHistoryRepositoryImpl
import com.maksimowiczm.foodyou.foodsearch.infrastructure.FoodSearchPreferencesRepositoryImpl
import com.maksimowiczm.foodyou.foodsearch.infrastructure.room.FoodSearchDatabase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val foodSearchModule = module {
    factoryOf(::SearchQueryParser)
    factoryOf(::FoodSearchPreferencesRepositoryImpl).bind<FoodSearchPreferencesRepository>()

    factory { get<FoodSearchDatabase>().searchHistoryDao }
    factoryOf(::FoodSearchHistoryRepositoryImpl).bind<FoodSearchHistoryRepository>()
}
