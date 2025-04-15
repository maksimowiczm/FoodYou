package com.maksimowiczm.foodyou.core.domain

import com.maksimowiczm.foodyou.core.domain.repository.FoodRepository
import com.maksimowiczm.foodyou.core.domain.repository.FoodRepositoryImpl
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepositoryImpl
import com.maksimowiczm.foodyou.core.domain.repository.SearchRepository
import com.maksimowiczm.foodyou.core.domain.repository.SearchRepositoryImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::FoodRepositoryImpl).bind<FoodRepository>()
    factoryOf(::SearchRepositoryImpl).bind<SearchRepository>()
    factoryOf(::MeasurementRepositoryImpl).bind<MeasurementRepository>()
}
