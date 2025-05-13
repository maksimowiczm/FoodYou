package com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts

import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductRequest

internal class OpenFoodFactsProductRequest(
    private val dataSource: OpenFoodFactsRemoteDataSource,
    private val barcode: String
) : RemoteProductRequest {
    override suspend fun getProduct(): Result<RemoteProduct> = dataSource.getProduct(barcode)
}
