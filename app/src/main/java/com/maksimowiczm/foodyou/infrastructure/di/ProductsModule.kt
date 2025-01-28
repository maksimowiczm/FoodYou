package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.product.data.ProductRepository
import com.maksimowiczm.foodyou.feature.product.data.ProductRepositoryImpl
import com.maksimowiczm.foodyou.feature.product.network.RemoteProductDatabase
import com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.OpenFoodFactsNetworkDataSource
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProductViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val productsModule = module {
    viewModelOf(::CreateProductViewModel)

    factoryOf(::ProductRepositoryImpl).bind<ProductRepository>()

    factory {
        OpenFoodFactsDatabase(
            dataStore = get(),
            productDatabase = get(),
            openFoodFactsNetworkDataSource = OpenFoodFactsNetworkDataSource()
        )
    }.bind<RemoteProductDatabase>()
}
