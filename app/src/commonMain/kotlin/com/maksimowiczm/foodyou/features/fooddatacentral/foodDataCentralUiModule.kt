package com.maksimowiczm.foodyou.features.fooddatacentral

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val foodDataCentralUiModule = module {
    viewModelOf(::FoodDataCentralSettingsViewModel)
}
