package com.maksimowiczm.foodyou.app.ui

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appUiModule = module { viewModelOf(::AppViewModel) }
