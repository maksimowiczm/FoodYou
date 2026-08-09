package com.maksimowiczm.foodyou.features.diary

import com.maksimowiczm.foodyou.features.diary.search.FoodDiarySearchViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.diary() {
    viewModelOf(::FoodDiarySearchViewModel)
}
