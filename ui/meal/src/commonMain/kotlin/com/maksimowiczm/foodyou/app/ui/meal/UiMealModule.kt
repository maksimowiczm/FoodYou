package com.maksimowiczm.foodyou.app.ui.meal

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiMealModule = module { viewModelOf(::MealSettingsViewModel) }
