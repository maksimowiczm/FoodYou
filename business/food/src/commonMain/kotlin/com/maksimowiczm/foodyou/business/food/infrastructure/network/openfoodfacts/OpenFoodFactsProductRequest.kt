package com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts

import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteFoodException
import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteProduct
import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteProductRequest
import com.maksimowiczm.foodyou.shared.common.result.Err
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result

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
