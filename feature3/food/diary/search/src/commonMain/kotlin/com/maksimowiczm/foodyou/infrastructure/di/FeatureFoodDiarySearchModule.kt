package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.diary.search.presentation.DiaryFoodSearchViewModel
import com.maksimowiczm.foodyou.feature.food.diary.search.usecase.ObserveRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.diary.search.usecase.ObserveRecipeUseCaseImpl
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureFoodDiarySearchModule = module {
    viewModelOf(::DiaryFoodSearchViewModel)
    scope<DiaryFoodSearchViewModel> {
        scopedOf(::ObserveRecipeUseCaseImpl).bind<ObserveRecipeUseCase>()
    }
}
