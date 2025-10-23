package com.maksimowiczm.foodyou.food.infrastructure.usda

import com.maksimowiczm.foodyou.food.infrastructure.usda.network.FoodDataCentralRateLimiter
import com.maksimowiczm.foodyou.food.infrastructure.usda.network.FoodDataCentralRemoteDataSource
import com.maksimowiczm.foodyou.food.infrastructure.usda.room.FoodDataCentralDatabase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.onClose

const val FOOD_DATA_CENTRAL_DATABASE_NAME = "FoodDataCentralDatabase.db"

expect fun Scope.foodDataCentralDatabase(): FoodDataCentralDatabase

fun Module.foodDataCentralModule() {
    single { foodDataCentralDatabase() }
    factory { get<FoodDataCentralDatabase>().dao }

    singleOf(::FoodDataCentralRateLimiter)
    single(named("FoodDataCentralRemoteDataSource")) {
            HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }
        }
        .onClose { it?.close() }
    factory {
        FoodDataCentralRemoteDataSource(
            client = get(named("FoodDataCentralRemoteDataSource")),
            rateLimiter = get(),
            networkConfig = get(),
            logger = get(),
        )
    }
    factoryOf(::FoodDataCentralRepository)
}
