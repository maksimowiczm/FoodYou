package com.maksimowiczm.foodyou.common.di

import com.maksimowiczm.foodyou.app.ui.common.utility.FoodNameSelector
import com.maksimowiczm.foodyou.common.infrastructure.food.AndroidFoodNameSelector
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

internal actual fun Module.foodNameSelector(): KoinDefinition<out FoodNameSelector> =
    factoryOf(::AndroidFoodNameSelector)
