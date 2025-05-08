package com.maksimowiczm.foodyou.feature.meal

import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsWithSummaryUseCase
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsWithSummaryUseCaseImpl
import com.maksimowiczm.foodyou.feature.meal.ui.card.MealsCardViewModel
import com.maksimowiczm.foodyou.feature.meal.ui.screen.MealScreenViewModel
import com.maksimowiczm.foodyou.feature.meal.ui.settings.MealsSettingsScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mealModule = module {
    factoryOf(::ObserveMealsWithSummaryUseCaseImpl).bind<ObserveMealsWithSummaryUseCase>()

    viewModelOf(::MealsSettingsScreenViewModel)
    viewModelOf(::MealScreenViewModel)
    viewModelOf(::MealsCardViewModel)
}
