package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network

import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodPageResponse

interface OpenFoodFactsRemoteDataSource {
    suspend fun getProduct(barcode: String, countries: String? = null): Result<OpenFoodFactsProduct>

    suspend fun queryProducts(
        query: String,
        countries: String? = null,
        page: Int? = null,
        pageSize: Int = 50
    ): OpenFoodPageResponse
}
