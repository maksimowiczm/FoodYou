package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.settings.meal.presentation.MealSettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureSettingsMealModule = module { viewModelOf(::MealSettingsViewModel) }
