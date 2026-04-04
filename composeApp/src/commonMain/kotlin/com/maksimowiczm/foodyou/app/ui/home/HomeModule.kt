package com.maksimowiczm.foodyou.app.ui.home

import com.maksimowiczm.foodyou.app.ui.home.search.HomeSearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::HomeOrderViewModel)
    viewModelOf(::HomeSearchViewModel)
}
