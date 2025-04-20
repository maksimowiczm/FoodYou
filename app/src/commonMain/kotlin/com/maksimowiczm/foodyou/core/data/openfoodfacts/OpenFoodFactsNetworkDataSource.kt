package com.maksimowiczm.foodyou.core.data.openfoodfacts

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.core.data.openfoodfacts.model.OpenFoodFactsPageResponse
import com.maksimowiczm.foodyou.core.data.openfoodfacts.model.v1.OpenFoodFactsPageResponseV1
import com.maksimowiczm.foodyou.core.data.openfoodfacts.model.v2.OpenFoodFactsProductResponseV2
import com.maksimowiczm.foodyou.core.domain.model.openfoodfacts.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.core.domain.source.OpenFoodFactsRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent

internal class OpenFoodFactsNetworkDataSource(private val client: HttpClient) :
    OpenFoodFactsRemoteDataSource {

    override suspend fun getProduct(code: String, country: String?): OpenFoodFactsProduct? {
        val countries = country?.lowercase()

        val url = "${BuildConfig.OPEN_FOOD_FACTS_URL}api/v2/product/$code"
        val response = client.get(url) {
            userAgent(BuildConfig.OPEN_FOOD_FACTS_USER_AGENT)

            timeout {
                requestTimeoutMillis = TIMEOUT
                connectTimeoutMillis = TIMEOUT
                socketTimeoutMillis = TIMEOUT
            }

            countries?.let { parameter("countries", countries) }
            parameter("fields", FIELDS)
        }

        if (response.status == HttpStatusCode.NotFound) {
            Logger.d(TAG) { "Product not found for code: $code" }
            return null
        }

        return response.body<OpenFoodFactsProductResponseV2>().product
    }

    // This was the part of the in app search
    @Suppress("Unused")
    suspend fun queryProducts(
        query: String,
        country: String?,
        page: Int?,
        pageSize: Int
    ): OpenFoodFactsPageResponse {
        val countries = country?.lowercase()

        val url = "${BuildConfig.OPEN_FOOD_FACTS_URL}cgi/search.pl"
        val response = client.get(url) {
            userAgent(BuildConfig.OPEN_FOOD_FACTS_USER_AGENT)
            parameter("search_terms", query)
            countries?.let { parameter("countries", countries) }
            parameter("page", page)
            parameter("page_size", pageSize)
            parameter("fields", FIELDS)
        }

        return response.body<OpenFoodFactsPageResponseV1>()
    }

    private companion object {
        private const val TAG = "OpenFoodFactsNetworkDataSource"
        private const val TIMEOUT = 10_000L
    }
}

private const val FIELDS = "" +
    "product_name" +
    ",code" +
    ",nutriments" +
    ",brands" +
    ",serving_quantity" +
    ",serving_quantity_unit" +
    ",product_quantity" +
    ",product_quantity_unit"
