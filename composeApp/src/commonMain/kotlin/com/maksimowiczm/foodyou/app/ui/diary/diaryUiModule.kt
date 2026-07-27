package com.maksimowiczm.foodyou.app.ui.diary

import com.maksimowiczm.foodyou.app.ui.diary.search.FoodDiarySearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val diaryUiModule = module {
    viewModelOf(::FoodDiarySearchViewModel)
}
