package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.externaldatabase.usda.USDARemoteDataSource
import com.maksimowiczm.foodyou.externaldatabase.usda.USDARemoteDataSourceImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.qualifier
import org.koin.dsl.bind
import org.koin.dsl.module

val usdaModule = module {
    single(qualifier("USDA")) {
        HttpClient {
            install(HttpTimeout)
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }

    factory { USDARemoteDataSourceImpl(client = get(qualifier("USDA"))) }
        .bind<USDARemoteDataSource>()
}
