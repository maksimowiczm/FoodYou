package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.common.infrastructure.koin.eventHandlerOf
import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import org.koin.core.module.Module
import org.koin.dsl.bind

fun Module.foodSearchDomainModule() {
    factory {
            FoodSearchUseCase(
                foodSearchRepository = get(),
                foodSearchPreferencesRepository = userPreferencesRepository(),
                foodRemoteMediatorFactoryAggregate = get(),
                dateProvider = get(),
                eventBus = get(),
            )
        }
        .bind<FoodSearchUseCase>()

    eventHandlerOf(::FoodSearchEventHandler)
}
