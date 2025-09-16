package com.maksimowiczm.foodyou.app.ui.food.diary.search

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiFoodDiarySearchModule = module { viewModelOf(::DiaryFoodSearchViewModel) }
