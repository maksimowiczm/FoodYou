package com.maksimowiczm.foodyou.features.diary

import com.maksimowiczm.foodyou.features.diary.search.FoodDiarySearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val diaryUiModule = module {
    viewModelOf(::FoodDiarySearchViewModel)
}
