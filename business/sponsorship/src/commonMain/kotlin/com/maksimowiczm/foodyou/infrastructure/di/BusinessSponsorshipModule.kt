package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.sponsorship.application.command.AllowRemoteSponsorshipsCommandHandler
import com.maksimowiczm.foodyou.business.sponsorship.application.query.ObserveSponsorshipPreferencesQueryHandler
import com.maksimowiczm.foodyou.business.sponsorship.application.query.ObserveSponsorshipsQueryHandler
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.network.RemoteSponsorshipDataSource
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.network.ktor.KtorSponsorshipApiClient
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.persistence.LocalSponsorshipDataSource
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.persistence.room.RoomSponsorshipDataSource
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.preferences.SponsorshipPreferencesDataSource
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.preferences.datastore.DataStoreSponsorshipPreferencesDataSource
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
            KtorSponsorshipApiClient(
                client = get(named("ktorSponsorshipHttpClient")),
                networkConfig = get(),
            )
        }
        .bind<RemoteSponsorshipDataSource>()
    factoryOf(::RoomSponsorshipDataSource).bind<LocalSponsorshipDataSource>()

    factoryOf(::DataStoreSponsorshipPreferencesDataSource).bind<SponsorshipPreferencesDataSource>()

    commandHandlerOf(::AllowRemoteSponsorshipsCommandHandler)
    queryHandlerOf(::ObserveSponsorshipsQueryHandler)
    queryHandlerOf(::ObserveSponsorshipPreferencesQueryHandler)
}
