package com.maksimowiczm.foodyou.feature.product.data.network.usda

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.product.BuildConfig
import com.maksimowiczm.foodyou.feature.product.data.ProductNotFoundException
import com.maksimowiczm.foodyou.feature.product.data.network.usda.model.AbridgedFoodItem
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent

internal class USDAProductRequest(
    private val client: HttpClient,
    private val apiKey: String,
    private val id: String,
    private val mapper: USDAProductMapper = USDAProductMapper
) : RemoteProductRequest {
    override suspend fun execute(): Result<RemoteProduct> {
        try {
            val url = "${BuildConfig.USDA_URL}/fdc/v1/food/$id"

            val response = client.get(url) {
                userAgent(BuildConfig.USER_AGENT)

                parameter("format", "abridged")
                parameter("api_key", apiKey)
            }

            if (response.status == HttpStatusCode.NotFound) {
                Logger.d(TAG) { "Product not found for code: $id" }
                return Result.failure(ProductNotFoundException())
            }

            val product = response.body<AbridgedFoodItem>()

            return Result.success(mapper.toRemoteProduct(product))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private companion object {
        private const val TAG = "USDAProductRequest"
    }
}
