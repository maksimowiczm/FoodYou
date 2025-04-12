package com.maksimowiczm.foodyou.core.repository

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    factoryOf(::FoodRepositoryImpl).bind<FoodRepository>()

    factoryOf(::MeasurementRepositoryImpl).bind<MeasurementRepository>()
    factoryOf(::GoalsRepositoryImpl).bind<GoalsRepository>()
    factoryOf(::SearchRepositoryImpl).bind<SearchRepository>()
}
