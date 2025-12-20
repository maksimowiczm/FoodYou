package com.maksimowiczm.foodyou.food.infrastructure.usda

import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.common.result.Err
import com.maksimowiczm.foodyou.common.result.Ok
import com.maksimowiczm.foodyou.common.result.Result
import com.maksimowiczm.foodyou.food.domain.entity.RemoteFoodException
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProductRequest
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferences
import kotlinx.coroutines.flow.first

internal class USDAProductRequest(
    private val dataSource: UsdaFdcDataSource,
    private val id: String,
    private val mapper: UsdaFdcMapper,
    private val preferencesRepository: UserPreferencesRepository<FoodSearchPreferences>,
) : RemoteProductRequest {
    private suspend fun apiKey() = preferencesRepository.observe().first().usda.apiKey

    override suspend fun execute(): Result<RemoteProduct, RemoteFoodException> =
        dataSource
            .getFood(id, apiKey = apiKey())
            .map(mapper::toRemoteProduct)
            .fold(onSuccess = ::Ok, onFailure = { Err(RemoteFoodException.fromThrowable(it)) })
}
