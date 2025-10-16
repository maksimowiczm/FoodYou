package com.maksimowiczm.foodyou.food.di

import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.openFoodFactsModule
import com.maksimowiczm.foodyou.food.infrastructure.user.userFoodModule
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

val foodModule = module {
    openFoodFactsModule()
    userFoodModule()
    foodNameSelector().bind<FoodNameSelector>()
}

expect fun Module.foodNameSelector(): KoinDefinition<out FoodNameSelector>
