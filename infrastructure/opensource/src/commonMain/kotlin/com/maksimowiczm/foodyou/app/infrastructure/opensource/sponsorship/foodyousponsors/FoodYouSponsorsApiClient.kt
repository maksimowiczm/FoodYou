package com.maksimowiczm.foodyou.app.infrastructure.opensource.sponsorship.foodyousponsors

import com.maksimowiczm.foodyou.app.business.opensource.domain.config.NetworkConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.userAgent
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// https://github.com/maksimowiczm/FoodYou-sponsors API client
@OptIn(ExperimentalTime::class)
internal class FoodYouSponsorsApiClient(
    private val client: HttpClient,
    private val config: NetworkConfig,
) {
    suspend fun getSponsorships(
        before: Instant? = null,
        after: Instant? = null,
        size: Int = 20,
    ): PagedSponsorshipsResponse {
        val url = "${config.sponsorshipApiUrl}/sponsorships"

        val response =
            client.get(url) {
                timeout {
                    requestTimeoutMillis = TIMEOUT
                    connectTimeoutMillis = TIMEOUT
                    socketTimeoutMillis = TIMEOUT
                }

                userAgent(config.userAgent)

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
