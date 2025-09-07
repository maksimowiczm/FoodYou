package com.maksimowiczm.foodyou.business.food.di

import com.maksimowiczm.foodyou.business.food.domain.ExportCsvProductsUseCase
import com.maksimowiczm.foodyou.business.food.domain.ExportCsvProductsUseCaseImpl
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchEventHandler
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferences
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchUseCase
import com.maksimowiczm.foodyou.business.food.domain.ImportCsvProductUseCase
import com.maksimowiczm.foodyou.business.food.domain.ImportCsvProductUseCaseImpl
import com.maksimowiczm.foodyou.business.food.domain.ImportSwissFoodCompositionDatabaseUseCase
import com.maksimowiczm.foodyou.business.food.domain.ImportSwissFoodCompositionDatabaseUseCaseImpl
import com.maksimowiczm.foodyou.business.shared.di.eventHandlerOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.qualifier
import org.koin.dsl.bind
import org.koin.dsl.module

val businessFoodModule = module {
    factoryOf(::ExportCsvProductsUseCaseImpl).bind<ExportCsvProductsUseCase>()
    factoryOf(::ImportCsvProductUseCaseImpl).bind<ImportCsvProductUseCase>()
    factoryOf(::ImportSwissFoodCompositionDatabaseUseCaseImpl)
        .bind<ImportSwissFoodCompositionDatabaseUseCase>()

    // TODO
    //  eventHandlerOf(::FoodDiaryEntryCreatedEventHandler)
    eventHandlerOf(::FoodSearchEventHandler)

    factory {
            FoodSearchUseCase(
                foodSearchRepository = get(),
                foodSearchPreferencesRepository =
                    get(qualifier(FoodSearchPreferences::class.qualifiedName!!)),
                foodRemoteMediatorFactoryAggregate = get(),
                dateProvider = get(),
                eventBus = get(),
            )
        }
        .bind<FoodSearchUseCase>()
}
