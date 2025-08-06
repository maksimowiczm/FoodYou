package com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts

import com.maksimowiczm.foodyou.business.food.domain.RemoteProduct
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductRequest
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.feature.usda.USDAException

internal class OpenFoodFactsProductRequest(
    private val dataSource: OpenFoodFactsRemoteDataSource,
    private val barcode: String,
    private val mapper: OpenFoodFactsProductMapper,
) : RemoteProductRequest {
    override suspend fun execute(): Result<RemoteProduct?> =
        dataSource
            .getProduct(barcode)
            .map { mapper.toRemoteProduct(it) }
            .onFailure {
                if (it is USDAException.ProductNotFoundException) {
                    return Result.success(null)
                }
            }
}
