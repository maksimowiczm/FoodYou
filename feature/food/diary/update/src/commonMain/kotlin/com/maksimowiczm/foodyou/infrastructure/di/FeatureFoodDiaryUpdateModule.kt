package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.diary.update.presentation.UpdateFoodDiaryEntryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureFoodDiaryUpdateModule = module { viewModelOf(::UpdateFoodDiaryEntryViewModel) }
