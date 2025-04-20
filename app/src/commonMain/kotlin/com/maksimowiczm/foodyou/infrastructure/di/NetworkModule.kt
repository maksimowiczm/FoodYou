package com.maksimowiczm.foodyou.infrastructure.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.dsl.onClose

val networkModule = module {
    single {
        HttpClient {
            install(HttpTimeout)
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }.onClose {
        it?.close()
    }
}
