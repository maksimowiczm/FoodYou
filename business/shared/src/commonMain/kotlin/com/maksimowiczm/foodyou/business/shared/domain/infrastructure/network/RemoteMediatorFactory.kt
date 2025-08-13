package com.maksimowiczm.foodyou.business.shared.domain.infrastructure.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator

@OptIn(ExperimentalPagingApi::class)
interface RemoteMediatorFactory {
    fun <K : Any, T : Any> create(): RemoteMediator<K, T>?
}
