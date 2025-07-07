package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodPageResponse
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.v1.OpenFoodFactsPageResponseV1
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.v2.OpenFoodFactsProductResponseV2
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.v2.OpenFoodFactsProductV2
import com.maksimowiczm.foodyou.feature.openfoodfacts.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent

internal class OpenFoodFactsRemoteDataSourceImpl(private val client: HttpClient) :
    OpenFoodFactsRemoteDataSource {

    override suspend fun getProduct(
        barcode: String,
        countries: String?
    ): Result<OpenFoodFactsProductV2> = runCatching {
        val countries = countries?.lowercase()
        val url = "${BuildConfig.OPEN_FOOD_FACTS_URL}/api/v2/product/$barcode"

        val response = client.get(url) {
            userAgent(BuildConfig.USER_AGENT)

            timeout {
                requestTimeoutMillis = TIMEOUT
                connectTimeoutMillis = TIMEOUT
                socketTimeoutMillis = TIMEOUT
            }

            countries?.let { parameter("countries", countries) }
            parameter("fields", FIELDS)
        }

        if (response.status == HttpStatusCode.Companion.NotFound) {
            Logger.Companion.d(TAG) { "Product not found for code: $barcode" }
            return Result.failure(ProductNotFoundException())
        }

        val product = response.body<OpenFoodFactsProductResponseV2>()

        return Result.success(product).map { it.product }
    }

    override suspend fun queryProducts(
        query: String,
        countries: String?,
        page: Int?,
        pageSize: Int
    ): OpenFoodPageResponse =
        client.get("${BuildConfig.OPEN_FOOD_FACTS_URL}/cgi/search.pl?search_simple=1&json=1") {
            parameter("search_terms", query)
            parameter("countries", countries)
            parameter("page", page)
            parameter("page_size", pageSize)
            parameter("fields", FIELDS)
        }.body<OpenFoodFactsPageResponseV1>()

    private companion object {
        private const val TAG = "OpenFoodFactsRemoteDataSource"
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
