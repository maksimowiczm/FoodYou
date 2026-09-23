package com.maksimowiczm.foodyou.features.openfoodfacts

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.openFoodFacts() {
    viewModelOf(::OpenFoodFactsLoginViewModel)
}
