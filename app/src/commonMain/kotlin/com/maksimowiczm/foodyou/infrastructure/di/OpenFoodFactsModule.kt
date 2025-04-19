package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.data.openfoodfacts.OpenFoodFactsNetworkDataSource
import com.maksimowiczm.foodyou.core.domain.source.OpenFoodFactsRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val openFoodFactsModule = module {
    singleOf(::OpenFoodFactsNetworkDataSource).bind<OpenFoodFactsRemoteDataSource>()
}
