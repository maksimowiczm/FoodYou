package com.maksimowiczm.foodyou.food.search.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery

@OptIn(ExperimentalPagingApi::class)
interface ProductRemoteMediatorFactory {
    suspend fun <K : Any, T : Any> create(
        query: SearchQuery,
        pageSize: Int,
        dietaryFilter: DietaryFilter? = null,
    ): RemoteMediator<K, T>?
}
