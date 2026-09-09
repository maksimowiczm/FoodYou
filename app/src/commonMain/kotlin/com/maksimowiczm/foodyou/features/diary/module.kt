package com.maksimowiczm.foodyou.features.diary

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.diary() {
    viewModelOf(::AddFoodDiaryEntryViewModel)
    viewModelOf(::UpdateFoodDiaryEntryViewModel)
    viewModelOf(::UpdateAnonymousFoodDiaryEntryViewModel)
}
