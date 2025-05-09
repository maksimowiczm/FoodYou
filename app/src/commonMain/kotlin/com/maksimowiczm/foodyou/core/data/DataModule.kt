package com.maksimowiczm.foodyou.core.data

import com.maksimowiczm.foodyou.core.data.openfoodfacts.OpenFoodFactsNetworkDataSource
import com.maksimowiczm.foodyou.core.data.openfoodfacts.OpenFoodFactsSharedProductRemoteDataSource
import com.maksimowiczm.foodyou.core.domain.source.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.core.domain.source.SharedProductRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

// This is a bit nonsense module because database is in the other module
val dataModule = module {
    singleOf(::OpenFoodFactsNetworkDataSource).bind<OpenFoodFactsRemoteDataSource>()

    factory {
        listOf(
            OpenFoodFactsSharedProductRemoteDataSource()
        )
    }.bind<List<SharedProductRemoteDataSource>>()
}
