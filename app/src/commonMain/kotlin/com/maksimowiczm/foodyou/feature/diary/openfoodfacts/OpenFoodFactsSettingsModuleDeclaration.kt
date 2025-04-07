package com.maksimowiczm.foodyou.feature.diary.openfoodfacts

import com.maksimowiczm.foodyou.feature.diary.core.network.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.diary.openfoodfacts.data.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.feature.diary.openfoodfacts.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.diary.openfoodfacts.ui.OpenFoodFactsSearchHintViewModel
import com.maksimowiczm.foodyou.feature.diary.openfoodfacts.ui.OpenFoodFactsSettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.bind

val openFoodFactsSettingsModuleDeclaration: ModuleDeclaration = {
    singleOf(::OpenFoodFactsRemoteMediatorFactory).bind<ProductRemoteMediatorFactory>()

    factoryOf(::OpenFoodFactsSettingsRepository)
    viewModelOf(::OpenFoodFactsSettingsViewModel)
    viewModelOf(::OpenFoodFactsSearchHintViewModel)
}
