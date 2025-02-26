package com.maksimowiczm.foodyou.network

import android.util.Log
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.network.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.network.model.OpenFoodPageResponse
import com.maksimowiczm.foodyou.network.model.v1.OpenFoodFactsPageResponseV1
import com.maksimowiczm.foodyou.network.model.v2.OpenFoodFactsProductResponseV2
import java.time.Duration
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal class OpenFoodFactsNetworkDataSource {
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
        .create(RetrofitOpenFoodFactsNetworkApi::class.java)

    suspend fun getProduct(code: String, country: String): OpenFoodFactsProduct? {
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
    ): OpenFoodPageResponse = networkApi.queryProducts(
        query = query,
        countries = country.lowercase(),
        page = page,
        pageSize = pageSize
    )

    private companion object {
        private const val TAG = "OpenFoodFactsNetworkDataSource"
        private const val TIMEOUT = 30L
    }
}

private const val FIELDS = "" +
    "product_name" +
    ",code" +
    ",nutriments" +
    ",image_url+" +
    ",brands" +
    ",serving_quantity" +
    ",serving_quantity_unit" +
    ",product_quantity" +
    ",product_quantity_unit"

private interface RetrofitOpenFoodFactsNetworkApi {
    @GET("api/v2/product/{code}?fields=$FIELDS")
    suspend fun getProduct(
        @Path("code") code: String,
        @Query("countries") countries: String
    ): OpenFoodFactsProductResponseV2

    @GET(
        "cgi/search.pl?search_simple=1&json=1&fields=$FIELDS"
    )
    suspend fun queryProducts(
        @Query("search_terms") query: String,
        @Query("countries") countries: String,
        @Query("page") page: Int?,
        @Query("page_size") pageSize: Int
    ): OpenFoodFactsPageResponseV1
}
