package com.maksimowiczm.foodyou.feature.mealredesign

import com.maksimowiczm.foodyou.feature.mealredesign.domain.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.mealredesign.domain.ObserveMealsUseCaseImpl
import com.maksimowiczm.foodyou.feature.mealredesign.ui.MealsCardsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mealModule = module {
    factoryOf(::ObserveMealsUseCaseImpl).bind<ObserveMealsUseCase>()

    viewModelOf(::MealsCardsViewModel)
}
