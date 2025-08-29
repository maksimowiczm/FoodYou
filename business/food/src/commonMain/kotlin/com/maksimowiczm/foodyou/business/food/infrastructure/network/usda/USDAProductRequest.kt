package com.maksimowiczm.foodyou.business.food.infrastructure.network.usda

import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteFoodException
import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteProduct
import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteProductRequest
import com.maksimowiczm.foodyou.shared.common.result.Err
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.first

internal class USDAProductRequest(
    private val dataSource: USDARemoteDataSource,
    private val id: String,
    private val mapper: USDAProductMapper,
    private val preferencesRepository: FoodSearchPreferencesRepository,
) : RemoteProductRequest {
    private suspend fun apiKey() = preferencesRepository.observe().first().usda.apiKey

    override suspend fun execute(): Result<RemoteProduct, RemoteFoodException> =
        dataSource
            .getProduct(id, apiKey())
            .map(mapper::toRemoteProduct)
            .fold(onSuccess = ::Ok, onFailure = { Err(RemoteFoodException.fromThrowable(it)) })
}
