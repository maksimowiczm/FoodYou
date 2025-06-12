package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.ui.about.AboutSettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AboutSettingsViewModel)
}
