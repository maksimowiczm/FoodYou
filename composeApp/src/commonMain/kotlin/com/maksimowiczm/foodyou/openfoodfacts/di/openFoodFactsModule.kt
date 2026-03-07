package com.maksimowiczm.foodyou.openfoodfacts.di

import com.maksimowiczm.foodyou.common.infrastructure.databaseBuilder
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.OpenFoodFactsRepositoryImpl
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsV2RemoteDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.SearchaliciousRemoteDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsDatabase.Companion.buildDatabase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

private const val OPEN_FOOD_FACTS_DATABASE_NAME = "OpenFoodFactsDatabase.db"
private val httpClientQualifier = named("OpenFoodFactsHttpClient")
private val searchaliciousRateLimiter = named("SearchaliciousRateLimiter")
private val openFoodFactsV2RateLimiter = named("OpenFoodFactsV2RateLimiter")

val openFoodFactsModule = module {
    single { databaseBuilder<OpenFoodFactsDatabase>(OPEN_FOOD_FACTS_DATABASE_NAME).buildDatabase() }
    factory { get<OpenFoodFactsDatabase>().dao }

    single(httpClientQualifier) {
            HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }
        }
        .onClose { it?.close() }

    single(searchaliciousRateLimiter) { SearchaliciousRemoteDataSource.rateLimiter(get()) }
    single(openFoodFactsV2RateLimiter) { OpenFoodFactsV2RemoteDataSource.rateLimiter(get()) }
    factory {
        SearchaliciousRemoteDataSource(
            client = get(httpClientQualifier),
            rateLimiter = get(searchaliciousRateLimiter),
            networkConfig = get(),
            logger = get(),
        )
    }
    factory {
        OpenFoodFactsV2RemoteDataSource(
            client = get(httpClientQualifier),
            rateLimiter = get(openFoodFactsV2RateLimiter),
            networkConfig = get(),
            logger = get(),
        )
    }

    factoryOf(::OpenFoodFactsRepositoryImpl).bind<OpenFoodFactsRepository>()
}
