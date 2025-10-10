package com.maksimowiczm.foodyou.app.ui.home

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module { viewModelOf(::ProfileViewModel) }
