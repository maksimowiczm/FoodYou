package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import com.maksimowiczm.foodyou.business.fooddiary.domain.ObserveDiaryMealsUseCase
import com.maksimowiczm.foodyou.business.fooddiary.domain.ObserveDiaryMealsUseCaseImpl
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.CreateFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.UnpackFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.UpdateFoodDiaryEntryUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val businessFoodDiaryModule = module {
    factoryOf(::CreateFoodDiaryEntryUseCase)
    factoryOf(::UnpackFoodDiaryEntryUseCase)
    factoryOf(::UpdateFoodDiaryEntryUseCase)

    factory {
            ObserveDiaryMealsUseCaseImpl(
                mealRepository = get(),
                mealsPreferencesRepository = get(named(MealsPreferences::class.qualifiedName!!)),
                foodEntryRepository = get(),
                manualEntryRepository = get(),
                dateProvider = get(),
            )
        }
        .bind<ObserveDiaryMealsUseCase>()
}
