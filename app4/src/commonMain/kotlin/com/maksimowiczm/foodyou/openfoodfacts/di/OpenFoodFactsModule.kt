package com.maksimowiczm.foodyou.openfoodfacts.di

import com.maksimowiczm.foodyou.common.infrastructure.databaseBuilder
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.OpenFoodFactsRepositoryImpl
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsRateLimiter
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsDatabase.Companion.buildDatabase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

private const val OPEN_FOOD_FACTS_DATABASE_NAME = "OpenFoodFactsDatabase.db"

val openFoodFactsModule = module {
    single { databaseBuilder<OpenFoodFactsDatabase>(OPEN_FOOD_FACTS_DATABASE_NAME).buildDatabase() }
    factory { get<OpenFoodFactsDatabase>().dao }

    single(named("OpenFoodFactsRemoteDataSource")) {
            HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }
        }
        .onClose { it?.close() }
    singleOf(::OpenFoodFactsRateLimiter)
    factory {
        OpenFoodFactsRemoteDataSource(
            client = get(named("OpenFoodFactsRemoteDataSource")),
            rateLimiter = get(),
            networkConfig = get(),
            logger = get(),
        )
    }

    factoryOf(::OpenFoodFactsRepositoryImpl).bind<OpenFoodFactsRepository>()
}
