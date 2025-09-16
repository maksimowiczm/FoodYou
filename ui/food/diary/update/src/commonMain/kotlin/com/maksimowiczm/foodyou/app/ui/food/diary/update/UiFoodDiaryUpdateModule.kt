package com.maksimowiczm.foodyou.app.ui.food.diary.update

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiFoodDiaryUpdateModule = module { viewModelOf(::UpdateFoodDiaryEntryViewModel) }
