package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.CreateFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.ObserveDiaryMealsUseCase
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.UnpackFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.UpdateFoodDiaryEntryUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

internal fun Module.foodDiaryDomainModule() {
    factoryOf(::CreateFoodDiaryEntryUseCase)
    factoryOf(::UnpackFoodDiaryEntryUseCase)
    factoryOf(::UpdateFoodDiaryEntryUseCase)

    factory {
        ObserveDiaryMealsUseCase(
            mealRepository = get(),
            mealsPreferencesRepository = userPreferencesRepository(),
            foodEntryRepository = get(),
            manualEntryRepository = get(),
            dateProvider = get(),
        )
    }
}
