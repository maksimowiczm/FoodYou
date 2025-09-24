package com.maksimowiczm.foodyou.app.ui.food.diary.update

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

fun Module.foodDiaryUpdate() {
    viewModelOf(::UpdateFoodDiaryEntryViewModel)
}
