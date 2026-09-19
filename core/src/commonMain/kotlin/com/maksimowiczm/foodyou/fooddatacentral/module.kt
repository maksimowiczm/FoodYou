package com.maksimowiczm.foodyou.fooddatacentral

import com.maksimowiczm.foodyou.common.extension.databaseBuilder
import com.maksimowiczm.foodyou.fooddatacentral.application.FoodDataCentralService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiKeyVerificationService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralRepository
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralUrlSearchQuery
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.FoodDataCentralRepositoryImpl
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.FoodDataCentralSettingsRepositoryImpl
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.FoodDataCentralRemoteDataSource
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralDatabase
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralDatabase.Companion.buildDatabase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.onClose

private const val FOOD_DATA_CENTRAL_DATABASE_NAME = "FoodDataCentralDatabase.db"
private val httpClientQualifier = named("FoodDataCentralHttpClient")
private val rateLimiter = named("FoodDataCentralRateLimiter")

internal fun Module.foodDataCentral() {
    single {
        databaseBuilder<FoodDataCentralDatabase>(FOOD_DATA_CENTRAL_DATABASE_NAME).buildDatabase()
    }
    factory { get<FoodDataCentralDatabase>().dao }

    single(httpClientQualifier) {
            HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }
        }
        .onClose { it?.close() }
    single(rateLimiter) { FoodDataCentralRemoteDataSource.rateLimiter() }
    factory {
        FoodDataCentralRemoteDataSource(
            client = get(httpClientQualifier),
            rateLimiter = get(rateLimiter),
            networkConfig = get(),
            logger = get(),
        )
    }
        .bind<FoodDataCentralApiKeyVerificationService>()

    factoryOf(::FoodDataCentralRepositoryImpl).bind<FoodDataCentralRepository>()

    factoryOf(::FoodDataCentralService)

    factoryOf(::FoodDataCentralSettingsRepositoryImpl).bind<FoodDataCentralSettingsRepository>()

    single(named(FoodDataCentralUrlSearchQuery::class.qualifiedName!!)) {
        FoodDataCentralUrlSearchQuery.recognizer
    }
}
