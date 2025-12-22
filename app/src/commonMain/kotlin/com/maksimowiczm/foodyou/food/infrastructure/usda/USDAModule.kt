package com.maksimowiczm.foodyou.food.infrastructure.usda

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named

internal fun Module.USDAModule() {
    single(named(USDARemoteDataSource::class.qualifiedName!!)) {
        HttpClient {
            install(HttpTimeout)
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }
    factory {
        USDARemoteDataSource(
            client = get(named(USDARemoteDataSource::class.qualifiedName!!)),
            get(),
            get(),
        )
    }
    factory {
        USDAFacade(
            dataSource = get(),
            mapper = get(),
            preferencesRepository = userPreferencesRepository(),
            logger = get(),
        )
    }
    factoryOf(::USDAMapper)
}
