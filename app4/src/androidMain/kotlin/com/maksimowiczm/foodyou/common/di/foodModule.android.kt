package com.maksimowiczm.foodyou.common.di

import com.maksimowiczm.foodyou.common.domain.food.FoodNameSelector
import com.maksimowiczm.foodyou.common.infrastructure.food.AndroidFoodNameSelector
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

internal actual fun Module.foodNameSelector(): KoinDefinition<out FoodNameSelector> =
    factoryOf(::AndroidFoodNameSelector)
