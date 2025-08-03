package com.maksimowiczm.foodyou.feature.about.data.network

import FoodYou.feature.about.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.userAgent
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal interface SponsorshipApiClient {
    suspend fun getSponsorships(
        before: Instant? = null,
        after: Instant? = null,
        size: Int = 20
    ): PagedSponsorshipsResponse
}

@OptIn(ExperimentalTime::class)
internal class SponsorshipApiClientImpl(private val client: HttpClient) : SponsorshipApiClient {
    override suspend fun getSponsorships(
        before: Instant?,
        after: Instant?,
        size: Int
    ): PagedSponsorshipsResponse {
        val url = "${BuildConfig.SPONSOR_API_URL}/sponsorships"

        val response = client.get(url) {
            timeout {
                requestTimeoutMillis = TIMEOUT
                connectTimeoutMillis = TIMEOUT
                socketTimeoutMillis = TIMEOUT
            }

            userAgent(BuildConfig.SPONSOR_API_USER_AGENT)

            if (before != null) {
                parameter("before", before)
            }

            if (after != null) {
                parameter("after", after)
            }

            parameter("size", size)
        }

        val pagedResponse = response.body<PagedSponsorshipsResponse>()

        return pagedResponse
    }

    private companion object {
        private const val TIMEOUT = 10_000L
    }
}
