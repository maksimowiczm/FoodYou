package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.feature.product.data.network.usda.USDAFacade
import com.maksimowiczm.foodyou.feature.product.domain.ProductRepository
import com.maksimowiczm.foodyou.feature.product.domain.ProductRepositoryImpl
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductRequestFactoryImpl
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProductScreenViewModel
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenViewModel
import com.maksimowiczm.foodyou.feature.product.ui.update.UpdateProductScreenViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val productModule = module {
    // USDA
    factory {
        USDAFacade(
            httpClient = HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }
        )
    }

    // Open Food Facts
    factory {
        OpenFoodFactsRemoteDataSource(
            client = HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }
        )
    }

    factory {
        OpenFoodFactsFacade(
            remoteDataSource = get()
        )
    }

    factoryOf(::RemoteProductRequestFactoryImpl).bind<RemoteProductRequestFactory>()

    factoryOf(::ProductRepositoryImpl).bind<ProductRepository>()

    viewModelOf(::CreateProductScreenViewModel)
    viewModelOf(::UpdateProductScreenViewModel)
    viewModel { (text: String?) ->
        DownloadProductScreenViewModel(text = text, requestFactory = get())
    }
}
