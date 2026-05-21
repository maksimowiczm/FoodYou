package com.maksimowiczm.foodyou.openfoodfacts.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.Result
import kotlinx.coroutines.flow.Flow

interface OpenFoodFactsRepository {
    fun search(
        parameters: OpenFoodFactsSearchParameters,
        pageSize: Int,
        remoteEnabled: Boolean,
    ): Flow<PagingData<OpenFoodFactsProduct>>

    fun count(parameters: OpenFoodFactsSearchParameters): Flow<Int>

    fun observe(
        identity: OpenFoodFactsProductIdentity,
        remoteEnabled: Boolean,
    ): Flow<RemoteData<OpenFoodFactsProduct>>

    suspend fun refresh(
        identity: OpenFoodFactsProductIdentity
    ): Result<OpenFoodFactsProduct, OpenFoodFactsApiError>
}
