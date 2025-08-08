package com.maksimowiczm.foodyou.business.food.infrastructure.network.usda

import com.maksimowiczm.foodyou.business.food.domain.RemoteProduct
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductRequest
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.LocalFoodPreferencesDataSource
import com.maksimowiczm.foodyou.externaldatabase.usda.USDAException
import com.maksimowiczm.foodyou.externaldatabase.usda.USDARemoteDataSource
import kotlinx.coroutines.flow.first

internal class USDAProductRequest(
    private val dataSource: USDARemoteDataSource,
    private val id: String,
    private val mapper: USDAProductMapper,
    private val preferencesSource: LocalFoodPreferencesDataSource,
) : RemoteProductRequest {
    private suspend fun apiKey() = preferencesSource.observe().first().usda.apiKey

    override suspend fun execute(): Result<RemoteProduct?> =
        dataSource
            .getProduct(id, apiKey())
            .map { mapper.toRemoteProduct(it) }
            .onFailure {
                if (it is USDAException.ProductNotFoundException) {
                    return Result.success(null)
                }
            }
}
