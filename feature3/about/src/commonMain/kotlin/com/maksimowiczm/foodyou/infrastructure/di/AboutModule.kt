package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.about.data.network.SponsorshipApiClient
import com.maksimowiczm.foodyou.feature.about.data.network.SponsorshipApiClientImpl
import com.maksimowiczm.foodyou.feature.about.ui.SponsorMessagesViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val aboutModule = module {
    viewModelOf(::SponsorMessagesViewModel)

    factory {
        SponsorshipApiClientImpl(
            client = HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }
        )
    }.bind<SponsorshipApiClient>()
}
