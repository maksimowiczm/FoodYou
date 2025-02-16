package com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts

import com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts.model.v1.OpenFoodFactsPageResponseV1
import com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts.model.v2.OpenFoodFactsProductResponseV2
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

internal interface OpenFoodFactsNetworkApi {
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
