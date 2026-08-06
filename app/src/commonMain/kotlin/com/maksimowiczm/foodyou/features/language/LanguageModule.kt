package com.maksimowiczm.foodyou.features.language

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val languageModule = module { viewModelOf(::LanguageViewModel) }
