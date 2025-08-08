package com.maksimowiczm.foodyou.externaldatabase.openfoodfacts

import com.maksimowiczm.foodyou.externaldatabase.openfoodfacts.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.externaldatabase.openfoodfacts.model.OpenFoodPageResponse

interface OpenFoodFactsRemoteDataSource {
    suspend fun getProduct(barcode: String, countries: String? = null): Result<OpenFoodFactsProduct>

    suspend fun queryProducts(
        query: String,
        countries: String? = null,
        page: Int? = null,
        pageSize: Int = 50,
    ): OpenFoodPageResponse
}
