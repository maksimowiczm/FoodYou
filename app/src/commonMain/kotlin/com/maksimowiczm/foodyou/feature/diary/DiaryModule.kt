package com.maksimowiczm.foodyou.feature.diary

import com.maksimowiczm.foodyou.feature.diary.data.meal.MealRepository
import com.maksimowiczm.foodyou.feature.diary.data.meal.MealRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.mealssettings.domain.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.diary.mealssettings.domain.ObserveMealsUseCaseImpl
import com.maksimowiczm.foodyou.feature.diary.mealssettings.ui.MealsSettingsScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val diaryModule = module {
    factoryOf(::MealRepositoryImpl).bind<MealRepository>()

    // Meals Settings
    viewModelOf(::MealsSettingsScreenViewModel)
    factoryOf(::ObserveMealsUseCaseImpl).bind<ObserveMealsUseCase>()
}
