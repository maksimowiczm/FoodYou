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
        credentials: OpenFoodFactsCredentials.Decrypted?,
        onNewProduct: suspend (Set<OpenFoodFactsProduct>) -> Unit,
    ): Flow<PagingData<OpenFoodFactsProduct>>

    fun count(parameters: OpenFoodFactsSearchParameters): Flow<Int>

    fun observe(
        id: OpenFoodFactsProductId,
        remoteEnabled: Boolean,
    ): Flow<RemoteData<OpenFoodFactsProduct>>

    suspend fun refresh(
        id: OpenFoodFactsProductId
    ): Result<OpenFoodFactsProduct, OpenFoodFactsApiError>
}
