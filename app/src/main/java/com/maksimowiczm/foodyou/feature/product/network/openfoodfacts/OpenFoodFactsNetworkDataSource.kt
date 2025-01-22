package com.maksimowiczm.foodyou.feature.product.network.openfoodfacts

import com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.model.OpenFoodPageResponse
import com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.model.OpenFoodProduct

interface OpenFoodFactsNetworkDataSource {
    suspend fun getProduct(code: String, country: String): OpenFoodProduct?
    suspend fun queryProducts(
        query: String,
        country: String,
        page: Int?,
        pageSize: Int
    ): OpenFoodPageResponse
}
