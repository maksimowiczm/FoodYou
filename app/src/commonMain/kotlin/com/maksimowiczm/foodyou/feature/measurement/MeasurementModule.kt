package com.maksimowiczm.foodyou.feature.measurement

import com.maksimowiczm.foodyou.feature.measurement.domain.ObserveMeasurableFoodUseCase
import com.maksimowiczm.foodyou.feature.measurement.domain.ObserveMeasurableFoodUseCaseImpl
import com.maksimowiczm.foodyou.feature.measurement.ui.CreateMeasurementScreenViewModel
import com.maksimowiczm.foodyou.feature.measurement.ui.UpdateMeasurementScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val measurementModule = module {
    factoryOf(::ObserveMeasurableFoodUseCaseImpl).bind<ObserveMeasurableFoodUseCase>()
    viewModelOf(::CreateMeasurementScreenViewModel)
    viewModelOf(::UpdateMeasurementScreenViewModel)
}
