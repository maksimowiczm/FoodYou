package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.about.ui.AboutSettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val aboutModule = module {
    viewModelOf(::AboutSettingsViewModel)
}
