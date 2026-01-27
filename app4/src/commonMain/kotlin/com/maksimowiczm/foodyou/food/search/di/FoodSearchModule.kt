package com.maksimowiczm.foodyou.food.search.di

import com.maksimowiczm.foodyou.food.search.domain.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.food.search.domain.SearchQueryParser
import com.maksimowiczm.foodyou.food.search.infrastructure.FoodSearchHistoryRepositoryImpl
import com.maksimowiczm.foodyou.food.search.infrastructure.FoodSearchPreferencesRepositoryImpl
import com.maksimowiczm.foodyou.food.search.infrastructure.room.FoodSearchDatabase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val foodSearchModule = module {
    factoryOf(::SearchQueryParser)
    factoryOf(::FoodSearchPreferencesRepositoryImpl).bind<FoodSearchPreferencesRepository>()

    factory { get<FoodSearchDatabase>().searchHistoryDao }
    factoryOf(::FoodSearchHistoryRepositoryImpl).bind<FoodSearchHistoryRepository>()
}
