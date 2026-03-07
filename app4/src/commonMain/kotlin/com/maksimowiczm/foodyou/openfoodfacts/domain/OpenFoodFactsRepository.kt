package com.maksimowiczm.foodyou.openfoodfacts.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.RemoteData
import kotlinx.coroutines.flow.Flow

interface OpenFoodFactsRepository {
    fun search(
        parameters: OpenFoodFactsSearchParameters,
        pageSize: Int,
    ): Flow<PagingData<OpenFoodFactsProduct>>

    fun count(parameters: OpenFoodFactsSearchParameters): Flow<Int>

    fun observe(identity: OpenFoodFactsProductIdentity): Flow<RemoteData<OpenFoodFactsProduct>>

    suspend fun refresh(
        identity: OpenFoodFactsProductIdentity
    ): Result<OpenFoodFactsProduct, OpenFoodFactsApiError>
}
