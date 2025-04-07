package com.maksimowiczm.foodyou.feature.diary.core

import com.maksimowiczm.foodyou.feature.diary.core.data.food.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.core.data.meal.MealRepository
import com.maksimowiczm.foodyou.feature.diary.core.data.meal.MealRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.core.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.diary.core.network.ProductRemoteMediatorFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.bind

val coreModuleDeclaration: ModuleDeclaration = {
    factoryOf(::MealRepositoryImpl).bind<MealRepository>()
    singleOf(::OpenFoodFactsRemoteMediatorFactory).bind<ProductRemoteMediatorFactory>()

    factory {
        // TODO
        object : ProductRepository {
            override fun deleteUnusedOpenFoodFactsProducts() = Unit
        } as ProductRepository
    }
}
