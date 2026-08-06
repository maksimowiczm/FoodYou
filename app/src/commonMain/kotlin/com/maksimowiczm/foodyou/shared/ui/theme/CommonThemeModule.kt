package com.maksimowiczm.foodyou.shared.ui.theme

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val commonThemeModule = module { viewModelOf(::ThemeViewModel) }
