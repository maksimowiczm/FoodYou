package com.maksimowiczm.foodyou.fooddatacentral.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.LoadStatus
import kotlinx.coroutines.flow.Flow

interface FoodDataCentralRepository {
    fun search(
        parameters: FoodDataCentralSearchParameters,
        pageSize: Int,
    ): Flow<PagingData<FoodDataCentralProduct>>

    fun count(parameters: FoodDataCentralSearchParameters): Flow<Int>

    fun observe(identity: FoodDataCentralProductIdentity): Flow<LoadStatus<FoodDataCentralProduct>>

    suspend fun refresh(
        identity: FoodDataCentralProductIdentity
    ): Result<FoodDataCentralProduct, FoodDataCentralApiError>
}
