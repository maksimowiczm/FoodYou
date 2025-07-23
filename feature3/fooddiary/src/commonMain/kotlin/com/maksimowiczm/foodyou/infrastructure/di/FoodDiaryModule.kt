package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMealsUseCaseImpl
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMeasurementSuggestionsUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMeasurementSuggestionsUseCaseImpl
import com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.card.MealsCardsViewModel
import com.maksimowiczm.foodyou.feature.fooddiary.ui.measure.CreateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.fooddiary.ui.measure.UpdateMeasurementViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val foodDiaryModule = module {
    viewModelOf(::CreateMeasurementViewModel)
    factoryOf(::ObserveMealsUseCaseImpl).bind<ObserveMealsUseCase>()
    viewModelOf(::UpdateMeasurementViewModel)
    viewModelOf(::MealsCardsViewModel)
    factoryOf(
        ::ObserveMeasurementSuggestionsUseCaseImpl
    ).bind<ObserveMeasurementSuggestionsUseCase>()
}
