package com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts

import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductRequest

internal class OpenFoodFactsProductRequest(
    private val dataSource: OpenFoodFactsRemoteDataSource,
    private val barcode: String,
    private val mapper: OpenFoodFactsProductMapper = OpenFoodFactsProductMapper
) : RemoteProductRequest {
    override suspend fun execute(): Result<RemoteProduct> =
        dataSource.getProduct(barcode).map { mapper.toRemoteProduct(it) }
}
