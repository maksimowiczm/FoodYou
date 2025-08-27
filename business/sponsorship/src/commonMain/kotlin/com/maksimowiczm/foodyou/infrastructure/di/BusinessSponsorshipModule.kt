package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.sponsorship.domain.SponsorRepository
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.SponsorRepositoryImpl
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.datastore.DataStoreSponsorshipPreferencesDataSource
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.foodyousponsors.FoodYouSponsorsApiClient
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.room.RoomSponsorshipDataSource
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
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

    factoryOf(::DataStoreSponsorshipPreferencesDataSource)

    factoryOf(::SponsorRepositoryImpl).bind<SponsorRepository>()
}
