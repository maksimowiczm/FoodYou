package com.maksimowiczm.foodyou.core.repository

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    factoryOf(::FoodRepositoryImpl).bind<FoodRepository>()

    factory {
        SearchRepositoryImpl(
            database = get(),
            remoteMediatorFactory = get()
        )
    }.bind<SearchRepository>()

    factoryOf(::MeasurementRepositoryImpl).bind<MeasurementRepository>()
    factoryOf(::GoalsRepositoryImpl).bind<GoalsRepository>()
}
