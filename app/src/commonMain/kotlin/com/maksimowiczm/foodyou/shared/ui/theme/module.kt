package com.maksimowiczm.foodyou.shared.ui.theme

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

fun Module.commonTheme() {
    viewModelOf(::ThemeViewModel)
}
