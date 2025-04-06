package com.maksimowiczm.foodyou.feature.diary.openfoodfactssettings

import com.maksimowiczm.foodyou.feature.diary.openfoodfactssettings.data.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.feature.diary.openfoodfactssettings.ui.OpenFoodFactsSettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.ModuleDeclaration

val openFoodFactsSettingsDeclaration: ModuleDeclaration = {
    factoryOf(::OpenFoodFactsSettingsRepository)
    viewModelOf(::OpenFoodFactsSettingsViewModel)
}
