package com.maksimowiczm.foodyou.feature.measurement

import com.maksimowiczm.foodyou.feature.measurement.ui.CreateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.measurement.ui.UpdateMeasurementViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val measurementModule = module {
    factoryOf(::ObserveMeasurableFoodUseCaseImpl).bind<ObserveMeasurableFoodUseCase>()

    viewModelOf(::CreateMeasurementViewModel)
    viewModelOf(::UpdateMeasurementViewModel)
}
