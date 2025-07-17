package com.maksimowiczm.foodyou.feature.food.data.network.usda

import com.maksimowiczm.foodyou.feature.food.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProductRequest
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.ProductNotFoundException
import com.maksimowiczm.foodyou.feature.usda.USDARemoteDataSource

internal class USDAProductRequest(
    private val dataSource: USDARemoteDataSource,
    private val apiKey: String,
    private val id: String,
    private val mapper: USDAProductMapper
) : RemoteProductRequest {
    override suspend fun execute(): Result<RemoteProduct?> = dataSource
        .getProduct(id, apiKey)
        .map { mapper.toRemoteProduct(it) }
        .onFailure {
            if (it is ProductNotFoundException) {
                return Result.success(null)
            }
        }
}
