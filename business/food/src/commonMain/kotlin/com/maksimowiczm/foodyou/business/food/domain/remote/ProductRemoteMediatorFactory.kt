package com.maksimowiczm.foodyou.business.food.domain.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.food.domain.QueryType

@OptIn(ExperimentalPagingApi::class)
interface ProductRemoteMediatorFactory {
    suspend fun <K : Any, T : Any> create(query: QueryType, pageSize: Int): RemoteMediator<K, T>?
}
