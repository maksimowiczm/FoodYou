package com.maksimowiczm.foodyou.business.shared.application.infrastructure.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator

@OptIn(ExperimentalPagingApi::class)
interface RemoteMediatorFactory {
    fun <K : Any, T : Any> create(): RemoteMediator<K, T>?
}
