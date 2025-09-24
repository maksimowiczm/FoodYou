package com.maksimowiczm.foodyou.app.ui.food.diary.add

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

fun Module.foodDiaryAdd() {
    viewModelOf(::AddEntryViewModel)
}
