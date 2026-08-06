package com.maksimowiczm.foodyou.features.home

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::HomeOrderViewModel)
}
