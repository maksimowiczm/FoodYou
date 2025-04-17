package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.data.openfoodfacts.OpenFoodFactsNetworkDataSource
import com.maksimowiczm.foodyou.core.domain.source.OpenFoodFactsRemoteDataSource
import org.koin.dsl.bind
import org.koin.dsl.module

val openFoodFactsModule = module {
    single { OpenFoodFactsNetworkDataSource() }.bind<OpenFoodFactsRemoteDataSource>()
}
