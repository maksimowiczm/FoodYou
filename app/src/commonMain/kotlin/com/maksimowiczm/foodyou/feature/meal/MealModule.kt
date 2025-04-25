package com.maksimowiczm.foodyou.feature.meal

import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealWithFoodUseCase
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealWithFoodUseCaseImpl
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsUseCaseImpl
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsWithSummaryUseCase
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsWithSummaryUseCaseImpl
import com.maksimowiczm.foodyou.feature.meal.ui.MealScreenViewModel
import com.maksimowiczm.foodyou.feature.meal.ui.MealsCardViewModel
import com.maksimowiczm.foodyou.feature.meal.ui.settings.MealsSettingsScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mealModule = module {
    factoryOf(::ObserveMealsUseCaseImpl).bind<ObserveMealsUseCase>()
    factoryOf(::ObserveMealsWithSummaryUseCaseImpl).bind<ObserveMealsWithSummaryUseCase>()
    factoryOf(::ObserveMealWithFoodUseCaseImpl).bind<ObserveMealWithFoodUseCase>()

    viewModelOf(::MealsSettingsScreenViewModel)
    viewModelOf(::MealScreenViewModel)
    viewModelOf(::MealsCardViewModel)
}
