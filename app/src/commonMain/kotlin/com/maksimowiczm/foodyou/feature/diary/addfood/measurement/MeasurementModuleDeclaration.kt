package com.maksimowiczm.foodyou.feature.diary.addfood.measurement

import com.maksimowiczm.foodyou.feature.diary.addfood.measurement.domain.ObserveMeasurableFoodUseCase
import com.maksimowiczm.foodyou.feature.diary.addfood.measurement.domain.ObserveMeasurableFoodUseCaseImpl
import com.maksimowiczm.foodyou.feature.diary.addfood.measurement.ui.CreateMeasurementScreenViewModel
import com.maksimowiczm.foodyou.feature.diary.addfood.measurement.ui.UpdateMeasurementScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.bind

val measurementModuleDeclaration: ModuleDeclaration = {
    factoryOf(::ObserveMeasurableFoodUseCaseImpl).bind<ObserveMeasurableFoodUseCase>()
    viewModelOf(::CreateMeasurementScreenViewModel)
    viewModelOf(::UpdateMeasurementScreenViewModel)
}
