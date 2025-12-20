package com.maksimowiczm.foodyou.food.infrastructure.usda2

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named

internal fun Module.UsdaFdc2Module() {
    single(named(UsdaFdcDataSource::class.qualifiedName!!)) {
        HttpClient {
            install(HttpTimeout)
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }
    factory {
        UsdaFdcDataSource(
            client = get(named(UsdaFdcDataSource::class.qualifiedName!!)),
            networkConfig = get(),
            logger = get(),
            apiKey = null, // Can be configured via settings or BuildConfig
        )
    }
    factoryOf(::UsdaFdcMapper)
}
