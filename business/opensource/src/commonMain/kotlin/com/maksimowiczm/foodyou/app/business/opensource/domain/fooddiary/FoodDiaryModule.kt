package com.maksimowiczm.foodyou.app.business.opensource.domain.fooddiary

import com.maksimowiczm.foodyou.app.business.shared.di.eventHandlerOf
import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import org.koin.core.module.Module
import org.koin.dsl.bind

fun Module.foodDiaryModule() {
    factory {
            ObserveDiaryMealsUseCaseImpl(
                mealRepository = get(),
                mealsPreferencesRepository = userPreferencesRepository(),
                foodEntryRepository = get(),
                manualEntryRepository = get(),
                dateProvider = get(),
            )
        }
        .bind<ObserveDiaryMealsUseCase>()

    eventHandlerOf(::FoodDiaryEntryCreatedEventHandler)
}
