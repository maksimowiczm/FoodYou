package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.language.ui.LanguageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val languageModule = module {
    viewModel {
        LanguageViewModel(
            androidSystemDetails = get(),
            dataStore = get()
        )
    }
}
