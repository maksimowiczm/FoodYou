package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.diary.quickadd.CreateQuickAddViewModel
import com.maksimowiczm.foodyou.feature.food.diary.quickadd.UpdateQuickAddViewModel
import kotlinx.datetime.LocalDate
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureFoodDiaryQuickAddModule = module {
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
