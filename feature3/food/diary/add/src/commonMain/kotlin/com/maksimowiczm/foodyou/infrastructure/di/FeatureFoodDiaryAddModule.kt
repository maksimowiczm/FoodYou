package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.diary.add.presentation.AddEntryViewModel
import com.maksimowiczm.foodyou.feature.food.diary.add.usecase.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.food.diary.add.usecase.ObserveMealsUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureFoodDiaryAddModule = module {
    viewModelOf(::AddEntryViewModel)
    factoryOf(::ObserveMealsUseCaseImpl).bind<ObserveMealsUseCase>()
}
