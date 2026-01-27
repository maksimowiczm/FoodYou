package com.maksimowiczm.foodyou.fooddatacentral.di

import com.maksimowiczm.foodyou.common.infrastructure.databaseBuilder
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralRepository
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.FoodDataCentralRepositoryImpl
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.FoodDataCentralSettingsRepositoryImpl
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.FoodDataCentralRateLimiter
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.FoodDataCentralRemoteDataSource
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralDatabase
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralDatabase.Companion.buildDatabase
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

private const val FOOD_DATA_CENTRAL_DATABASE_NAME = "FoodDataCentralDatabase.db"

val foodDataCentralModule = module {
    single {
        databaseBuilder<FoodDataCentralDatabase>(FOOD_DATA_CENTRAL_DATABASE_NAME).buildDatabase()
    }
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

    factoryOf(::FoodDataCentralRepositoryImpl).bind<FoodDataCentralRepository>()

    factoryOf(::FoodDataCentralSettingsRepositoryImpl).bind<FoodDataCentralSettingsRepository>()
}
