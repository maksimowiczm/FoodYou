package com.maksimowiczm.foodyou.app.ui.language

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel

fun Module.language() {
    viewModel {
        LanguageViewModel(
            translationRepository = get(),
            settingsRepository = userPreferencesRepository(),
        )
    }
}
