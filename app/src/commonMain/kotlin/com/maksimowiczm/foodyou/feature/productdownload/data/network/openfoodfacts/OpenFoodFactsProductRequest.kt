package com.maksimowiczm.foodyou.feature.productdownload.data.network.openfoodfacts

import com.maksimowiczm.foodyou.feature.productdownload.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.productdownload.domain.RemoteProductRequest

internal class OpenFoodFactsProductRequest(
    private val dataSource: OpenFoodFactsRemoteDataSource,
    private val barcode: String
) : RemoteProductRequest {
    override suspend fun getProduct(): Result<RemoteProduct> = dataSource.getProduct(barcode)
}
