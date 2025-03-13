package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.ui.DiaryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureModule = module {
    // TODO move somewhere else
    // -- Shared
    viewModelOf(::DiaryViewModel)
}
