package com.maksimowiczm.foodyou.app.ui.meal

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

fun Module.meal() {
    viewModelOf(::MealSettingsViewModel)
}
