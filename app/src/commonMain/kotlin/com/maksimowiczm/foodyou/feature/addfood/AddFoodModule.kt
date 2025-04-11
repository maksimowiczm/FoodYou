package com.maksimowiczm.foodyou.feature.addfood

import com.maksimowiczm.foodyou.feature.addfood.data.SearchRepository
import com.maksimowiczm.foodyou.feature.addfood.domain.ObserveMeasurableFoodUseCase
import com.maksimowiczm.foodyou.feature.addfood.domain.ObserveMeasurableFoodUseCaseImpl
import com.maksimowiczm.foodyou.feature.addfood.ui.measurement.CreateMeasurementScreenViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.measurement.UpdateMeasurementScreenViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val addFoodModule = module {
    viewModelOf(::SearchFoodViewModel)
    viewModelOf(::CreateMeasurementScreenViewModel)
    viewModelOf(::UpdateMeasurementScreenViewModel)

    factoryOf(::ObserveMeasurableFoodUseCaseImpl).bind<ObserveMeasurableFoodUseCase>()

    factory {
        SearchRepository(
            database = get(),
            remoteMediatorFactory = get()
        )
    }
}
