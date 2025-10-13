package com.maksimowiczm.foodyou.food.search.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface SearchableFoodRepository {
    fun search(parameters: SearchParameters, pageSize: Int): Flow<PagingData<SearchableFoodDto>>
}
