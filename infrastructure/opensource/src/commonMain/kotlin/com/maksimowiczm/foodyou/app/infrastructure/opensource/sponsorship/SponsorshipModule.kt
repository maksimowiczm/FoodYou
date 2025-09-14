package com.maksimowiczm.foodyou.app.infrastructure.opensource.sponsorship

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepositoryOf
import com.maksimowiczm.foodyou.app.infrastructure.opensource.sponsorship.foodyousponsors.FoodYouSponsorsApiClient
import com.maksimowiczm.foodyou.sponsorship.domain.repository.SponsorRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.onClose

internal fun Module.sponsorshipModule() {
    single(named("ktorSponsorshipHttpClient")) {
            HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }
        }
        .onClose { it?.close() }

    factory {
        FoodYouSponsorsApiClient(client = get(named("ktorSponsorshipHttpClient")), config = get())
    }

    userPreferencesRepositoryOf(::DataStoreSponsorshipPreferencesDataSource)

    factory {
            SponsorRepositoryImpl(
                sponsorshipDao = get(),
                networkDataSource = get(),
                preferences = userPreferencesRepository(),
                logger = get(),
            )
        }
        .bind<SponsorRepository>()
}
