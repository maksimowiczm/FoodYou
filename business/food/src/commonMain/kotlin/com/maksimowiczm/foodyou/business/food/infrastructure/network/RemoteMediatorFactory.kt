package com.maksimowiczm.foodyou.business.food.infrastructure.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator

@OptIn(ExperimentalPagingApi::class)
internal interface RemoteMediatorFactory {
    fun <T : Any> create(): RemoteMediator<Int, T>?
}
