package com.maksimowiczm.foodyou.feature.diary

import com.maksimowiczm.foodyou.feature.diary.data.food.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.meal.MealRepository
import com.maksimowiczm.foodyou.feature.diary.data.meal.MealRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.mealscard.mealsCardModuleDeclaration
import com.maksimowiczm.foodyou.feature.diary.mealssettings.mealsSettingsModuleDeclaration
import com.maksimowiczm.foodyou.feature.diary.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.diary.network.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.diary.openfoodfactssettings.openFoodFactsSettingsDeclaration
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val diaryModule = module {
    factoryOf(::MealRepositoryImpl).bind<MealRepository>()
    singleOf(::OpenFoodFactsRemoteMediatorFactory).bind<ProductRemoteMediatorFactory>()

    factory {
        // TODO
        object : ProductRepository {
            override fun deleteUnusedOpenFoodFactsProducts() = Unit
        } as ProductRepository
    }

    mealsSettingsModuleDeclaration()
    mealsCardModuleDeclaration()
    openFoodFactsSettingsDeclaration()
}
