package com.maksimowiczm.foodyou.feature.meal

import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsUseCaseImpl
import com.maksimowiczm.foodyou.feature.meal.ui.card.MealsCardsViewModel
import com.maksimowiczm.foodyou.feature.meal.ui.settings.MealsSettingsScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mealModule = module {
    factoryOf(::ObserveMealsUseCaseImpl).bind<ObserveMealsUseCase>()

    viewModelOf(::MealsCardsViewModel)
    viewModelOf(::MealsSettingsScreenViewModel)
}
