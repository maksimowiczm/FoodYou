package com.maksimowiczm.foodyou.features.openfoodfacts

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val openFoodFactsModule = module {
    viewModelOf(::OpenFoodFactsLoginViewModel)
}
