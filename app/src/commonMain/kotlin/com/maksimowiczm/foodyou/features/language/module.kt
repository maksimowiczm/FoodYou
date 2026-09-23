package com.maksimowiczm.foodyou.features.language

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.language() {
    viewModelOf(::LanguageViewModel)
}
