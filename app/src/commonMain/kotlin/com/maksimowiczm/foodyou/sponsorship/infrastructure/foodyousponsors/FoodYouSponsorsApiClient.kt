package com.maksimowiczm.foodyou.sponsorship.infrastructure.foodyousponsors

import com.maksimowiczm.foodyou.common.config.NetworkConfig
import com.maksimowiczm.foodyou.common.log.Logger
import com.maksimowiczm.foodyou.sponsorship.infrastructure.NetworkSponsorship
import com.maksimowiczm.foodyou.sponsorship.infrastructure.SponsorsNetworkDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.userAgent
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atStartOfDayIn

// https://github.com/maksimowiczm/FoodYou-sponsors API client
internal class FoodYouSponsorsApiClient(
    private val client: HttpClient,
    private val config: NetworkConfig,
    private val rateLimiter: SponsorRateLimiter,
    private val logger: Logger,
) : SponsorsNetworkDataSource {
    override suspend fun getSponsorships(yearMonth: YearMonth): List<NetworkSponsorship> {
        val before = yearMonth.lastDay.atStartOfDayIn(TimeZone.UTC)
        val after = yearMonth.firstDay.atStartOfDayIn(TimeZone.UTC)

        if (!rateLimiter.canMakeRequest(yearMonth)) {
            logger.w(TAG) { "Rate limiter is preventing a new request for $yearMonth" }
            return emptyList()
        }

        rateLimiter.recordRequest(yearMonth)

        return getSponsorships(before, after, size = 100).sponsorships
    }

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
        const val TIMEOUT = 10_000L
        const val TAG = "FoodYouSponsorsApiClient"
    }
}
