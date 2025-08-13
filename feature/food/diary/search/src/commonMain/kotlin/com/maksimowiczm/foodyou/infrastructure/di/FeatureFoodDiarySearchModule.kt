package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.diary.search.presentation.DiaryFoodSearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureFoodDiarySearchModule = module { viewModelOf(::DiaryFoodSearchViewModel) }
