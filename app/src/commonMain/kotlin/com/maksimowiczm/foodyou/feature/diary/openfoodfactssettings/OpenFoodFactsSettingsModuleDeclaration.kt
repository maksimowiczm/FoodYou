package com.maksimowiczm.foodyou.feature.diary.openfoodfactssettings

import com.maksimowiczm.foodyou.feature.diary.core.data.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.feature.diary.openfoodfactssettings.ui.OpenFoodFactsSettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.ModuleDeclaration

val openFoodFactsSettingsModuleDeclaration: ModuleDeclaration = {
    factoryOf(::OpenFoodFactsSettingsRepository)
    viewModelOf(::OpenFoodFactsSettingsViewModel)
}
