package com.maksimowiczm.foodyou.theme

import com.maksimowiczm.foodyou.app.ui.theme.ThemeSettingsViewModel
import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepositoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val themeModule = module {
    userPreferencesRepositoryOf(::DataStoreThemeSettingsRepository)
    userPreferencesRepositoryOf(::DataStoreNutrientsColorsRepository)
    viewModel { ThemeSettingsViewModel(userPreferencesRepository(), userPreferencesRepository()) }
}
