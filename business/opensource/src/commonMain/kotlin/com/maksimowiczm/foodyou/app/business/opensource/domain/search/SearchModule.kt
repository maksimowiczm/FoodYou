package com.maksimowiczm.foodyou.app.business.opensource.domain.search

import com.maksimowiczm.foodyou.app.business.shared.di.eventHandlerOf
import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import org.koin.core.module.Module
import org.koin.dsl.bind

fun Module.searchModule() {
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
