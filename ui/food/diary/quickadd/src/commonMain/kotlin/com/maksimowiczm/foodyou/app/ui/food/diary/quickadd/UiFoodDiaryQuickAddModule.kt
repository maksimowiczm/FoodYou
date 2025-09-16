package com.maksimowiczm.foodyou.app.ui.food.diary.quickadd

import kotlinx.datetime.LocalDate
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiFoodDiaryQuickAddModule = module {
    viewModel { (mealId: Long, date: Long) ->
        CreateQuickAddViewModel(
            mealId = mealId,
            date = LocalDate.fromEpochDays(date),
            manualDiaryEntryRepository = get(),
            dateProvider = get(),
        )
    }
    viewModelOf(::UpdateQuickAddViewModel)
}
