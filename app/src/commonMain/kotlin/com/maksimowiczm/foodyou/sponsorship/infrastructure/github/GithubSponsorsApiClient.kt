package com.maksimowiczm.foodyou.sponsorship.infrastructure.github

import com.maksimowiczm.foodyou.common.config.NetworkConfig
import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import com.maksimowiczm.foodyou.sponsorship.infrastructure.NetworkSponsorship
import com.maksimowiczm.foodyou.sponsorship.infrastructure.SponsorsNetworkDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.userAgent
import kotlinx.datetime.YearMonth

internal class GithubSponsorsApiClient(
    private val httpClient: HttpClient,
    private val config: NetworkConfig,
    private val rateLimiter: RateLimiter,
) : SponsorsNetworkDataSource {
    override suspend fun getSponsorships(yearMonth: YearMonth): List<NetworkSponsorship> {
        if (rateLimiter.canMakeRequest()) rateLimiter.recordRequest()
        else error("Rate limit exceeded")

        val baseUrl = API_URL
        val month = yearMonth.month.ordinal + 1
        val path = "${yearMonth.year}/$month.json"
        val url = "${baseUrl}/$path"

        val response = httpClient.get(url) { userAgent(config.userAgent) }

        return response.body<List<NetworkSponsorship>>()
    }

    private companion object {
        private const val API_URL = "https://maksimowiczm.github.io/FoodYou-sponsors"
    }
}
