package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.diary.shared.usecase.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.food.diary.shared.usecase.ObserveMealsUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureFoodDiarySharedModule = module {
    factoryOf(::ObserveMealsUseCaseImpl).bind<ObserveMealsUseCase>()
}
