package com.maksimowiczm.foodyou.features.fooddatacentral

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.foodDataCentral() {
    viewModelOf(::FoodDataCentralSettingsViewModel)
}
