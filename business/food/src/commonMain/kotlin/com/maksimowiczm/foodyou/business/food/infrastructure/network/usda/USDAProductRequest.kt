package com.maksimowiczm.foodyou.business.food.infrastructure.network.usda

import com.maksimowiczm.foodyou.core.food.domain.entity.FoodSearchPreferences
import com.maksimowiczm.foodyou.core.food.domain.entity.RemoteFoodException
import com.maksimowiczm.foodyou.core.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.core.food.domain.entity.RemoteProductRequest
import com.maksimowiczm.foodyou.core.shared.Err
import com.maksimowiczm.foodyou.core.shared.Ok
import com.maksimowiczm.foodyou.core.shared.Result
import com.maksimowiczm.foodyou.core.shared.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.first

internal class USDAProductRequest(
    private val dataSource: USDARemoteDataSource,
    private val id: String,
    private val mapper: USDAProductMapper,
    private val preferencesRepository: UserPreferencesRepository<FoodSearchPreferences>,
) : RemoteProductRequest {
    private suspend fun apiKey() = preferencesRepository.observe().first().usda.apiKey

    override suspend fun execute(): Result<RemoteProduct, RemoteFoodException> =
        dataSource
            .getProduct(id, apiKey())
            .map(mapper::toRemoteProduct)
            .fold(onSuccess = ::Ok, onFailure = { Err(RemoteFoodException.fromThrowable(it)) })
}
