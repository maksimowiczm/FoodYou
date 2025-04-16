package com.maksimowiczm.foodyou.feature.openfoodfacts

import com.maksimowiczm.foodyou.feature.openfoodfacts.data.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.feature.openfoodfacts.data.ProductRepository
import com.maksimowiczm.foodyou.feature.openfoodfacts.ui.OpenFoodFactsSearchHintViewModel
import com.maksimowiczm.foodyou.feature.openfoodfacts.ui.OpenFoodFactsSettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val openFoodFactsSettingsModule = module {
    factoryOf(::ProductRepository)
    factoryOf(::OpenFoodFactsSettingsRepository)

    viewModelOf(::OpenFoodFactsSettingsViewModel)
    viewModelOf(::OpenFoodFactsSearchHintViewModel)
}
