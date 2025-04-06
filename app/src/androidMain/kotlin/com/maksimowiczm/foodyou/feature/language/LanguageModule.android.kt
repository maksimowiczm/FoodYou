package com.maksimowiczm.foodyou.feature.language

import com.maksimowiczm.foodyou.feature.language.ui.LanguageViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val languageModule = module {
    viewModelOf(::LanguageViewModel)
}
