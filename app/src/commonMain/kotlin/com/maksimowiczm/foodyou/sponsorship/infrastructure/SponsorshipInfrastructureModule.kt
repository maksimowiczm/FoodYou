package com.maksimowiczm.foodyou.sponsorship.infrastructure

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepositoryOf
import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import com.maksimowiczm.foodyou.sponsorship.domain.repository.SponsorRepository
import com.maksimowiczm.foodyou.sponsorship.infrastructure.github.GithubSponsorsApiClient
import com.maksimowiczm.foodyou.sponsorship.infrastructure.room.SponsorshipDatabase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.onClose

internal fun Module.sponsorshipInfrastructureModule() {
    single(named("ktorSponsorshipHttpClient")) {
            HttpClient { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }
        }
        .onClose { it?.close() }

    single(named("sponsorshipRateLimiter")) {
        RateLimiter(dateProvider = get(), maxRequests = 1, timeWindow = 1.seconds)
    }
    factory {
            GithubSponsorsApiClient(
                httpClient = get(named("ktorSponsorshipHttpClient")),
                config = get(),
                rateLimiter = get(named("sponsorshipRateLimiter")),
            )
        }
        .bind<SponsorsNetworkDataSource>()

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

    factory { database.sponsorshipDao }
}

private val Scope.database: SponsorshipDatabase
    get() = get()
