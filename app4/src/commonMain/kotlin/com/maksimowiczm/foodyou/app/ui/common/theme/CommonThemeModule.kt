package com.maksimowiczm.foodyou.app.ui.common.theme

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val commonThemeModule = module { viewModelOf(::ThemeViewModel) }
