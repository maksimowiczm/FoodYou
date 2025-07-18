package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.fooddiary.domain.FoodMapper
import com.maksimowiczm.foodyou.feature.fooddiary.domain.FoodMapperImpl
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMealsUseCaseImpl
import com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.card.MealsCardsViewModel
import com.maksimowiczm.foodyou.feature.fooddiary.ui.measure.CreateMeasurementScreenViewModel
import com.maksimowiczm.foodyou.feature.fooddiary.ui.measure.UpdateProductMeasurementViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val foodDiaryModule = module {
    factoryOf(::FoodMapperImpl).bind<FoodMapper>()
    viewModelOf(::CreateMeasurementScreenViewModel)
    factoryOf(::ObserveMealsUseCaseImpl).bind<ObserveMealsUseCase>()
    viewModelOf(::UpdateProductMeasurementViewModel)
    viewModelOf(::MealsCardsViewModel)
}
