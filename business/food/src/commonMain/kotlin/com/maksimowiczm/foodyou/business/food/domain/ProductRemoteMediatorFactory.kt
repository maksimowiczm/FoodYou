package com.maksimowiczm.foodyou.business.food.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.shared.domain.search.SearchQuery

@OptIn(ExperimentalPagingApi::class)
interface ProductRemoteMediatorFactory {
    suspend fun <K : Any, T : Any> create(query: SearchQuery, pageSize: Int): RemoteMediator<K, T>?
}
