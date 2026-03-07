package com.maksimowiczm.foodyou.userfood.domain.search

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface UserFoodSearchRepository {
    fun search(
        parameters: UserFoodSearchParameters,
        pageSize: Int,
    ): Flow<PagingData<UserFoodSearchItem>>

    fun count(parameters: UserFoodSearchParameters): Flow<Int>
}
