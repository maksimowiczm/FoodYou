package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.SponsorRepositoryImpl
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.datastore.DataStoreSponsorshipPreferencesDataSource
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.foodyousponsors.FoodYouSponsorsApiClient
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.room.RoomSponsorshipDataSource
import com.maksimowiczm.foodyou.core.shared.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.core.sponsorship.domain.entity.SponsorshipPreferences
import com.maksimowiczm.foodyou.core.sponsorship.domain.repository.SponsorRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

val sponsorshipPreferencesQualifier = named(SponsorshipPreferences::class.qualifiedName!!)

val businessSponsorshipModule = module {
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

    factoryOf(::RoomSponsorshipDataSource)

    factoryOf(::DataStoreSponsorshipPreferencesDataSource) {
            qualifier = sponsorshipPreferencesQualifier
        }
        .bind<UserPreferencesRepository<SponsorshipPreferences>>()

    factory {
            SponsorRepositoryImpl(
                localDataSource = get(),
                networkDataSource = get(),
                preferences = get(sponsorshipPreferencesQualifier),
                logger = get(),
            )
        }
        .bind<SponsorRepository>()
}
