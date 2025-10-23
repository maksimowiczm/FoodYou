package com.maksimowiczm.foodyou.food.di

import com.maksimowiczm.foodyou.food.domain.FoodDataCentralSettingsRepository
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository
import com.maksimowiczm.foodyou.food.infrastructure.FoodDataCentralSettingsRepositoryImpl
import com.maksimowiczm.foodyou.food.infrastructure.FoodProductRepositoryImpl
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.openFoodFactsModule
import com.maksimowiczm.foodyou.food.infrastructure.user.userFoodModule
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val foodModule = module {
    openFoodFactsModule()
    userFoodModule()
    foodNameSelector().bind<FoodNameSelector>()
    factoryOf(::FoodProductRepositoryImpl).bind<FoodProductRepository>()
    factoryOf(::FoodDataCentralSettingsRepositoryImpl).bind<FoodDataCentralSettingsRepository>()
}

expect fun Module.foodNameSelector(): KoinDefinition<out FoodNameSelector>
