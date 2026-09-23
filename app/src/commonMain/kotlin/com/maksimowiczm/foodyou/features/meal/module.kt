package com.maksimowiczm.foodyou.features.meal

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.meal() {
    viewModelOf(::MealScheduleViewModel)
}
