package com.maksimowiczm.foodyou.food.di

import com.maksimowiczm.foodyou.common.domain.FoodNameSelector
import com.maksimowiczm.foodyou.food.application.ObserveFoodsUseCase
import com.maksimowiczm.foodyou.food.domain.FoodDataCentralSettingsRepository
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository
import com.maksimowiczm.foodyou.food.infrastructure.FoodDataCentralSettingsRepositoryImpl
import com.maksimowiczm.foodyou.food.infrastructure.FoodProductRepositoryImpl
import com.maksimowiczm.foodyou.food.infrastructure.usda.foodDataCentralModule
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val foodModule = module {
    foodDataCentralModule()
    foodNameSelector().bind<FoodNameSelector>()
    factory { FoodProductRepositoryImpl(get()) }.bind<FoodProductRepository>()
    factoryOf(::FoodDataCentralSettingsRepositoryImpl).bind<FoodDataCentralSettingsRepository>()

    factoryOf(::ObserveFoodsUseCase)
}

expect fun Module.foodNameSelector(): KoinDefinition<out FoodNameSelector>
