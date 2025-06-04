package com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.product.BuildConfig
import com.maksimowiczm.foodyou.feature.product.data.ProductNotFoundException
import com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts.model.v2.OpenFoodFactsProductResponseV2
import com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts.model.v2.OpenFoodFactsProductV2
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent

internal class OpenFoodFactsRemoteDataSource(private val client: HttpClient) {

    suspend fun getProduct(
        barcode: String,
        countries: String? = null
    ): Result<OpenFoodFactsProductV2> = runCatching {
        val countries = countries?.lowercase()
        val url = "${BuildConfig.OPEN_FOOD_FACTS_URL}api/v2/product/$barcode"

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
            Logger.d(TAG) { "Product not found for code: $barcode" }
            return Result.failure(ProductNotFoundException())
        }

        val product = response.body<OpenFoodFactsProductResponseV2>()

        return Result.success(product).map { it.product }
    }

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
