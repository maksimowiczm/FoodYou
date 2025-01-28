package com.maksimowiczm.foodyou.feature.product.network.openfoodfacts

import android.util.Log
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.model.OpenFoodPageResponse
import com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.model.OpenFoodProduct
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.time.Duration

class OpenFoodFactsNetworkDataSource {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val httpClient = OkHttpClient.Builder()
        .callTimeout(Duration.ofSeconds(TIMEOUT))
        .addNetworkInterceptor {
            val request = it.request().newBuilder()
                // https://openfoodfacts.github.io/openfoodfacts-server/api/#authentication
                .header(
                    "User-Agent",
                    "FoodYou/${BuildConfig.VERSION_NAME}-${BuildConfig.FLAVOR} (${BuildConfig.CONTACT_EMAIL})"
                )
                .build()

            it.proceed(request)
        }
        .build()

    private val networkApi = Retrofit.Builder()
        .baseUrl(BuildConfig.OPEN_FOOD_FACTS_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(httpClient)
        .build()
        .create(OpenFoodFactsNetworkApi::class.java)

    suspend fun getProduct(code: String, country: String): OpenFoodProduct? {
        return try {
            networkApi.getProduct(
                code = code,
                countries = country.lowercase()
            ).product
        } catch (e: HttpException) {
            @Suppress("MagicNumber")
            if (e.code() == 404) {
                Log.d(TAG, "Product not found: $code")
                return null
            }

            throw e
        }
    }

    suspend fun queryProducts(
        query: String,
        country: String,
        page: Int?,
        pageSize: Int
    ): OpenFoodPageResponse {
        return networkApi.queryProducts(
            query = query,
            countries = country.lowercase(),
            page = page,
            pageSize = pageSize
        )
    }

    private companion object {
        private const val TAG = "OpenFoodFactsNetworkDataSource"
        private const val TIMEOUT = 30L
    }
}
