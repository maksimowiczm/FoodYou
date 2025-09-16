package com.maksimowiczm.foodyou.app.infrastructure.opensource.foodsearch

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepositoryOf
import com.maksimowiczm.foodyou.app.business.shared.domain.search.FoodRemoteMediatorFactoryAggregate
import com.maksimowiczm.foodyou.app.business.shared.domain.search.FoodSearchRepository
import com.maksimowiczm.foodyou.app.infrastructure.opensource.foodsearch.repository.DataStoreFoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.app.infrastructure.opensource.foodsearch.repository.RoomFoodSearchHistoryRepository
import com.maksimowiczm.foodyou.app.infrastructure.opensource.foodsearch.repository.RoomFoodSearchRepository
import com.maksimowiczm.foodyou.food.domain.repository.FoodSearchHistoryRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.foodSearchModule() {
    factoryOf(::FoodRemoteMediatorFactoryAggregateImpl).bind<FoodRemoteMediatorFactoryAggregate>()

    factoryOf(::RoomFoodSearchHistoryRepository).bind<FoodSearchHistoryRepository>()
    factoryOf(::RoomFoodSearchRepository).bind<FoodSearchRepository>()

    userPreferencesRepositoryOf(::DataStoreFoodSearchPreferencesRepository)
}
