package com.maksimowiczm.foodyou.features.settings

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.settings() {
    viewModelOf(::SettingsViewModel)
}
