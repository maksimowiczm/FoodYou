package com.maksimowiczm.foodyou.feature.measurement

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val measurementModule = module {
    factoryOf(::ObserveMeasurableFoodUseCaseImpl).bind<ObserveMeasurableFoodUseCase>()
}
