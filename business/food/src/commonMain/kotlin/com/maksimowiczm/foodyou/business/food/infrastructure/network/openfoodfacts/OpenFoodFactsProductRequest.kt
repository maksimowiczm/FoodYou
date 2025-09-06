package com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts

import com.maksimowiczm.foodyou.core.food.domain.entity.RemoteFoodException
import com.maksimowiczm.foodyou.core.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.core.food.domain.entity.RemoteProductRequest
import com.maksimowiczm.foodyou.core.shared.Err
import com.maksimowiczm.foodyou.core.shared.Ok
import com.maksimowiczm.foodyou.core.shared.Result

internal class OpenFoodFactsProductRequest(
    private val dataSource: OpenFoodFactsRemoteDataSource,
    private val barcode: String,
    private val mapper: OpenFoodFactsProductMapper,
) : RemoteProductRequest {
    override suspend fun execute(): Result<RemoteProduct, RemoteFoodException> =
        dataSource
            .getProduct(barcode)
            .map(mapper::toRemoteProduct)
            .fold(onSuccess = ::Ok, onFailure = { Err(RemoteFoodException.fromThrowable(it)) })
}
