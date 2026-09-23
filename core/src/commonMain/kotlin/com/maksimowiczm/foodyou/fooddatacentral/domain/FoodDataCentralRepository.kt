package com.maksimowiczm.foodyou.fooddatacentral.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.Result
import kotlinx.coroutines.flow.Flow

interface FoodDataCentralRepository {
    fun search(
        parameters: FoodDataCentralSearchParameters,
        pageSize: Int,
        remoteEnabled: Boolean,
        apiKey: String?,
        onNewProduct: suspend (Set<FoodDataCentralProduct>) -> Unit,
    ): Flow<PagingData<FoodDataCentralProduct>>

    fun count(parameters: FoodDataCentralSearchParameters): Flow<Int>

    fun observe(
        id: FoodDataCentralProductId,
        remoteEnabled: Boolean,
        apiKey: String?,
    ): Flow<RemoteData<FoodDataCentralProduct>>

    suspend fun refresh(
        id: FoodDataCentralProductId,
        apiKey: String?,
    ): Result<FoodDataCentralProduct, FoodDataCentralApiError>
}
