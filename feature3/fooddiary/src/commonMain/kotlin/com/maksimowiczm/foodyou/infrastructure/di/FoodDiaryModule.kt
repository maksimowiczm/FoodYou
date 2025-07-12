package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.fooddiary.domain.FoodMapper
import com.maksimowiczm.foodyou.feature.fooddiary.domain.FoodMapperImpl
import com.maksimowiczm.foodyou.feature.fooddiary.ui.measure.CreateMeasurementScreenViewModel
import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.FoodSearchViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val foodDiaryModule = module {
    viewModelOf(::FoodSearchViewModel)
    factoryOf(::FoodMapperImpl).bind<FoodMapper>()
    viewModelOf(::CreateMeasurementScreenViewModel)
}
