package com.maksimowiczm.foodyou.features.home

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.home() {
    viewModelOf(::HomeViewModel)
}
