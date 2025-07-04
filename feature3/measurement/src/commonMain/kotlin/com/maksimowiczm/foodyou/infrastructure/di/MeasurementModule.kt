package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.domain.MeasurementMapper
import com.maksimowiczm.foodyou.feature.food.domain.MeasurementMapperImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val measurementModule = module {
    factoryOf(::MeasurementMapperImpl).bind<MeasurementMapper>()
}
