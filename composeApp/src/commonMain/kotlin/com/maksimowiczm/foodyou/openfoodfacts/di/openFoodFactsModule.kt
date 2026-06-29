package com.maksimowiczm.foodyou.openfoodfacts.di

import com.maksimowiczm.foodyou.common.extension.databaseBuilder
import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import com.maksimowiczm.foodyou.common.infrastructure.network.SuspendingRateLimiter
import com.maksimowiczm.foodyou.common.infrastructure.network.WindowedRequestLog
import com.maksimowiczm.foodyou.openfoodfacts.application.OpenFoodFactsService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsLoginService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsUrlSearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.OpenFoodFactsRepositoryImpl
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.OpenFoodFactsSettingsRepositoryImpl
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsApiV1SearchDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsApiV2ProductDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsSearchALiciousDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsDatabase.Companion.buildDatabase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

private const val OPEN_FOOD_FACTS_DATABASE_NAME = "OpenFoodFactsDatabase.db"
private val httpClientQualifier = named("OpenFoodFactsHttpClient")
private val openFoodFactsSearchRateLimiter = named("OpenFoodFactsSearchRateLimiter")
private val openFoodFactsProductRateLimiter = named("OpenFoodFactsProductRateLimiter")

val openFoodFactsModule = module {
    single { databaseBuilder<OpenFoodFactsDatabase>(OPEN_FOOD_FACTS_DATABASE_NAME).buildDatabase() }
    factory { get<OpenFoodFactsDatabase>().productDao }
    single(httpClientQualifier) {
            HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                install(HttpCookies) { storage = AcceptAllCookiesStorage() }
            }
        }
        .onClose { it?.close() }
    single<RateLimiter>(openFoodFactsSearchRateLimiter) {
        SuspendingRateLimiter(
            log = WindowedRequestLog(10, 1.minutes),
            timeout = 150.milliseconds,
            minWaitTime = 100.milliseconds,
        )
    }
    single<RateLimiter>(openFoodFactsProductRateLimiter) {
        SuspendingRateLimiter(
            log = WindowedRequestLog(15, 1.minutes),
            timeout = 1.seconds,
            minWaitTime = 100.milliseconds,
        )
    }

    factory {
            OpenFoodFactsApiV1SearchDataSource(
                get(httpClientQualifier),
                get(openFoodFactsSearchRateLimiter),
                get(),
                get(),
            )
        }
        .bind<OpenFoodFactsLoginService>()
    factory {
        OpenFoodFactsSearchALiciousDataSource(
            get(httpClientQualifier),
            get(openFoodFactsSearchRateLimiter),
            get(),
            get(),
        )
    }
    factory {
        OpenFoodFactsApiV2ProductDataSource(
            get(httpClientQualifier),
            get(openFoodFactsProductRateLimiter),
            get(),
            get(),
        )
    }
    factory { get<OpenFoodFactsDatabase>().pagingKeyV1Dao }
    factory { get<OpenFoodFactsDatabase>().pagingKeySearchALiciousDao }

    factoryOf(::OpenFoodFactsRepositoryImpl).bind<OpenFoodFactsRepository>()

    factoryOf(::OpenFoodFactsService)
    factory { OpenFoodFactsService(get(), get(), get()) }

    single(named(OpenFoodFactsUrlSearchQuery::class.qualifiedName!!)) {
        OpenFoodFactsUrlSearchQuery.recognizer
    }

    factoryOf(::OpenFoodFactsSettingsRepositoryImpl).bind<OpenFoodFactsSettingsRepository>()
}
