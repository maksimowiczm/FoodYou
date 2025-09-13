package com.maksimowiczm.foodyou.app.ui.theme

import com.maksimowiczm.foodyou.app.business.opensource.di.userPreferencesRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiThemeModule = module {
    viewModel { ThemeSettingsViewModel(settingsRepository = userPreferencesRepository()) }
}
