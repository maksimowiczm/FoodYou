package com.maksimowiczm.foodyou.business.food.di

import com.maksimowiczm.foodyou.business.food.domain.ExportCsvProductsUseCase
import com.maksimowiczm.foodyou.business.food.domain.ExportCsvProductsUseCaseImpl
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchEventHandler
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchUseCase
import com.maksimowiczm.foodyou.business.food.domain.ImportCsvProductUseCase
import com.maksimowiczm.foodyou.business.food.domain.ImportCsvProductUseCaseImpl
import com.maksimowiczm.foodyou.business.food.domain.ImportSwissFoodCompositionDatabaseUseCase
import com.maksimowiczm.foodyou.business.food.domain.ImportSwissFoodCompositionDatabaseUseCaseImpl
import com.maksimowiczm.foodyou.business.shared.di.eventHandlerOf
import com.maksimowiczm.foodyou.business.shared.di.userPreferencesRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val businessFoodModule = module {
    factoryOf(::ExportCsvProductsUseCaseImpl).bind<ExportCsvProductsUseCase>()
    factoryOf(::ImportCsvProductUseCaseImpl).bind<ImportCsvProductUseCase>()
    factoryOf(::ImportSwissFoodCompositionDatabaseUseCaseImpl)
        .bind<ImportSwissFoodCompositionDatabaseUseCase>()

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
