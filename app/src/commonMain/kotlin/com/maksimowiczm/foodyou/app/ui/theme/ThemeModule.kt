package com.maksimowiczm.foodyou.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import com.maksimowiczm.foodyou.theme.RandomColorProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind

fun Module.theme() {
    viewModel {
        ThemeSettingsViewModel(userPreferencesRepository(), userPreferencesRepository(), get())
    }
    factory { RandomColorProvider { Color((0xFF000000..0xFFFFFFFF).random()).value } }
        .bind<RandomColorProvider>()
}
