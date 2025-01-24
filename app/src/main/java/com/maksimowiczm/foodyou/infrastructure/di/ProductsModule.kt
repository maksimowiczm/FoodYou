package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.product.data.ProductRepository
import com.maksimowiczm.foodyou.feature.product.data.ProductRepositoryImpl
import com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.OpenFoodFactsNetworkDataSource
import com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.RetrofitOpenFoodFactsNetworkDataSource
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProductViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val productsModule = module {
    viewModelOf(::CreateProductViewModel)

    factoryOf(::ProductRepositoryImpl).bind<ProductRepository>()

    factoryOf(::RetrofitOpenFoodFactsNetworkDataSource).bind<OpenFoodFactsNetworkDataSource>()
}
