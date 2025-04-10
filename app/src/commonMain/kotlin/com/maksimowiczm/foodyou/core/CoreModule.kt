package com.maksimowiczm.foodyou.core

import com.maksimowiczm.foodyou.core.repository.FoodRepository
import com.maksimowiczm.foodyou.core.repository.FoodRepositoryImpl
import com.maksimowiczm.foodyou.core.repository.GoalsRepository
import com.maksimowiczm.foodyou.core.repository.GoalsRepositoryImpl
import com.maksimowiczm.foodyou.core.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.repository.MeasurementRepositoryImpl
import com.maksimowiczm.foodyou.core.repository.SearchRepository
import com.maksimowiczm.foodyou.core.repository.SearchRepositoryImpl
import com.maksimowiczm.foodyou.core.util.DateProvider
import com.maksimowiczm.foodyou.core.util.DateProviderImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun Module.systemDetails()

expect fun Module.dateFormatter()

val coreModule = module {
    // Repository
    factoryOf(::FoodRepositoryImpl).bind<FoodRepository>()

    factory {
        SearchRepositoryImpl(
            database = get(),
            remoteMediatorFactory = get()
        )
    }.bind<SearchRepository>()

    factoryOf(::MeasurementRepositoryImpl).bind<MeasurementRepository>()
    factoryOf(::GoalsRepositoryImpl).bind<GoalsRepository>()

    // Util
    single { DateProviderImpl() }.bind<DateProvider>()

    systemDetails()
    dateFormatter()
}
