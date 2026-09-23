package com.maksimowiczm.foodyou.capabilities.theme

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

fun Module.theme() {
    viewModelOf(::ThemeViewModel)
}
