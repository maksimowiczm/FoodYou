package com.maksimowiczm.foodyou.food.di

import com.maksimowiczm.foodyou.common.domain.FoodNameSelector
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

val foodModule = module { foodNameSelector().bind<FoodNameSelector>() }

expect fun Module.foodNameSelector(): KoinDefinition<out FoodNameSelector>
