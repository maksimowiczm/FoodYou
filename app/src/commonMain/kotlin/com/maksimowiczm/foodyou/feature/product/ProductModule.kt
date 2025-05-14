package com.maksimowiczm.foodyou.feature.product

import com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.feature.product.domain.ProductRepository
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductRequestFactoryImpl
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProductScreenViewModel
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenViewModel
import com.maksimowiczm.foodyou.feature.product.ui.update.UpdateProductScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val productModule = module {
    // Open Food Facts
    factoryOf(::OpenFoodFactsRemoteDataSource)
    factory {
        OpenFoodFactsFacade(
            remoteDataSource = get()
        )
    }

    factoryOf(::RemoteProductRequestFactoryImpl).bind<RemoteProductRequestFactory>()

    factoryOf(::ProductRepository)

    viewModelOf(::CreateProductScreenViewModel)
    viewModelOf(::UpdateProductScreenViewModel)
    viewModel { (text: String?) ->
        DownloadProductScreenViewModel(text = text, requestFactory = get())
    }
}
