package com.maksimowiczm.foodyou.features.diary

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf

internal fun Module.diary() {
    viewModel { params ->
        AddFoodDiaryEntryViewModel(
            initialMealId = params.get(),
            initialDate = params.getOrNull(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModelOf(::UpdateFoodDiaryEntryViewModel)
    viewModelOf(::UpdateAnonymousFoodDiaryEntryViewModel)
}
