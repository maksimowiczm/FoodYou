package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.diary.update.presentation.UpdateEntryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureFoodDiaryUpdateModule = module { viewModelOf(::UpdateEntryViewModel) }
