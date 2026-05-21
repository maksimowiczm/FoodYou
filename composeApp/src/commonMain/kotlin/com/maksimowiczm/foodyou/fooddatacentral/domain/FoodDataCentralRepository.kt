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
    ): Flow<PagingData<FoodDataCentralProduct>>

    fun count(parameters: FoodDataCentralSearchParameters): Flow<Int>

    fun observe(
        identity: FoodDataCentralProductIdentity,
        remoteEnabled: Boolean,
        apiKey: String?,
    ): Flow<RemoteData<FoodDataCentralProduct>>

    suspend fun refresh(
        identity: FoodDataCentralProductIdentity,
        apiKey: String?,
    ): Result<FoodDataCentralProduct, FoodDataCentralApiError>
}
