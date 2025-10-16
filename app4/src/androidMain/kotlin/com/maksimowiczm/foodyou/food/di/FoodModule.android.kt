package com.maksimowiczm.foodyou.food.di

import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import com.maksimowiczm.foodyou.food.infrastructure.AndroidFoodNameSelector
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

actual fun Module.foodNameSelector(): KoinDefinition<out FoodNameSelector> =
    factoryOf(::AndroidFoodNameSelector)
