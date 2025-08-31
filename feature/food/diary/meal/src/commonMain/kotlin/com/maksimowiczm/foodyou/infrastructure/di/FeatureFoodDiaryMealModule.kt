package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.diary.meal.MealSettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureFoodDiaryMealModule = module { viewModelOf(::MealSettingsViewModel) }
