package com.maksimowiczm.foodyou.business.fooddiary.di

import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import com.maksimowiczm.foodyou.business.fooddiary.domain.ObserveDiaryMealsUseCase
import com.maksimowiczm.foodyou.business.fooddiary.domain.ObserveDiaryMealsUseCaseImpl
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val businessFoodDiaryModule = module {
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
