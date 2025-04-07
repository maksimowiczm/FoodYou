package com.maksimowiczm.foodyou.feature.diary.core

import com.maksimowiczm.foodyou.feature.diary.core.data.food.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.core.data.meal.MealRepository
import com.maksimowiczm.foodyou.feature.diary.core.data.meal.MealRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.MeasurementRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.core.data.search.SearchRepository
import com.maksimowiczm.foodyou.feature.diary.core.data.search.SearchRepositoryImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.bind

val coreModuleDeclaration: ModuleDeclaration = {
    factoryOf(::MealRepositoryImpl).bind<MealRepository>()

    factory {
        SearchRepositoryImpl(
            database = get(),
            remoteMediatorFactory = get()
        )
    }.bind<SearchRepository>()

    factoryOf(::MeasurementRepositoryImpl).bind<MeasurementRepository>()

    // TODO real implementation
    factory {
        object : ProductRepository {
            override fun deleteUnusedOpenFoodFactsProducts() = Unit
        } as ProductRepository
    }
}
