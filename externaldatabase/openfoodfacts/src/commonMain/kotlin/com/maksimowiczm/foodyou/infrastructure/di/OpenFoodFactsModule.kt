package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.externaldatabase.openfoodfacts.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.externaldatabase.openfoodfacts.OpenFoodFactsRemoteDataSourceImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.qualifier
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

val openFoodFactsModule = module {
    single(qualifier("OpenFoodFactsClient")) {
            HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }
        }
        .onClose { it?.close() }

    factory { OpenFoodFactsRemoteDataSourceImpl(client = get(qualifier("OpenFoodFactsClient"))) }
        .bind<OpenFoodFactsRemoteDataSource>()
}
