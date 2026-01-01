package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

import com.maksimowiczm.foodyou.common.infrastructure.databaseBuilder
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.OpenFoodFactsRateLimiter
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsDatabase.Companion.buildDatabase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.onClose

private const val OPEN_FOOD_FACTS_DATABASE_NAME = "OpenFoodFactsDatabase.db"

internal fun Module.openFoodFactsModule() {
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
    factoryOf(::OpenFoodFactsRepository)
}
