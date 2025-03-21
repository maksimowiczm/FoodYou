package com.maksimowiczm.foodyou.feature.language

import com.maksimowiczm.foodyou.feature.language.ui.LanguageViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

actual fun Module.languageViewModel() {
    viewModelOf(::LanguageViewModel)
}
