package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

import com.maksimowiczm.foodyou.food.domain.repository.OpenFoodFactsCredentialsRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.onClose

internal fun Module.openFoodFactsModule() {
    single(named(OpenFoodFactsRemoteDataSource::class.qualifiedName!!)) {
            HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                install(HttpCookies) { storage = AcceptAllCookiesStorage() }
            }
        }
        .onClose { it?.close() }
    singleOf(::OpenFoodFactsRateLimiter)
    factory {
        OpenFoodFactsRemoteDataSource(
            client = get(named(OpenFoodFactsRemoteDataSource::class.qualifiedName!!)),
            get(),
            get(),
            get(),
            get(),
        )
    }
    factoryOf(::OpenFoodFactsFacade)
    factoryOf(::OpenFoodFactsProductMapper)
    factoryOf(::OpenFoodFactsCredentialsRepositoryImpl).bind<OpenFoodFactsCredentialsRepository>()
}
