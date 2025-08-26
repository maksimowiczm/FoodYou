package com.maksimowiczm.foodyou.business.sponsorship.infrastructure.network.ktor

import com.maksimowiczm.foodyou.business.shared.application.infrastructure.network.NetworkConfig
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.network.PagedSponsorshipsResponse
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.network.RemoteSponsorshipDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.userAgent
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal class KtorSponsorshipApiClient(
    private val client: HttpClient,
    private val networkConfig: NetworkConfig,
) : RemoteSponsorshipDataSource {
    override suspend fun getSponsorships(
        before: Instant?,
        after: Instant?,
        size: Int,
    ): PagedSponsorshipsResponse {
        val url = "${networkConfig.sponsorshipApiUrl}/sponsorships"

        val response =
            client.get(url) {
                timeout {
                    requestTimeoutMillis = TIMEOUT
                    connectTimeoutMillis = TIMEOUT
                    socketTimeoutMillis = TIMEOUT
                }

                userAgent(networkConfig.userAgent)

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
