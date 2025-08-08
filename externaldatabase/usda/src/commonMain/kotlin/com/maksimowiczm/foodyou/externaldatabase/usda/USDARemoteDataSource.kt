package com.maksimowiczm.foodyou.externaldatabase.usda

import com.maksimowiczm.foodyou.externaldatabase.usda.model.Food
import com.maksimowiczm.foodyou.externaldatabase.usda.model.UsdaFoodPageResponse

interface USDARemoteDataSource {
    suspend fun getProduct(id: String, apiKey: String?): Result<Food>

    suspend fun queryProducts(
        query: String,
        page: Int? = null,
        pageSize: Int = 50,
        apiKey: String?,
    ): UsdaFoodPageResponse
}
