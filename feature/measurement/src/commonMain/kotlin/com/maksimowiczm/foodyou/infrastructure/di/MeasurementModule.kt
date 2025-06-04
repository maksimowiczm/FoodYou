package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.measurement.ui.CreateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.measurement.ui.UpdateMeasurementViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val measurementModule = module {
    viewModelOf(::CreateMeasurementViewModel)
    viewModelOf(::UpdateMeasurementViewModel)
}
