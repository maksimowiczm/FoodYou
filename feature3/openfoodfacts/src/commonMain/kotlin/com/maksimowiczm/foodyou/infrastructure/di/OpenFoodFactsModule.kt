package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.domain.ObserveOpenFoodFactsProductCountUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.domain.ObserveOpenFoodFactsProductPagesUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.OpenFoodFactsRemoteDataSourceImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.qualifier
import org.koin.dsl.bind
import org.koin.dsl.module

val openFoodFactsModule = module {
    single(qualifier("OpenFoodFactsClient")) {
        HttpClient {
            install(HttpTimeout)
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    factory {
        OpenFoodFactsRemoteDataSourceImpl(
            client = get(
                qualifier("OpenFoodFactsClient")
            )
        )
    }.bind<OpenFoodFactsRemoteDataSource>()

    factoryOf(::ObserveOpenFoodFactsProductPagesUseCase)
    factoryOf(::ObserveOpenFoodFactsProductCountUseCase)
}
