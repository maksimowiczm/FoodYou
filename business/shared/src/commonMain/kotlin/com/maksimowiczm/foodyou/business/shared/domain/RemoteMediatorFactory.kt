package com.maksimowiczm.foodyou.business.shared.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator

// This exists because domain layer has to know about the RemoteMediator type. But it also doesn't
// know about any concrete implementation of it. That is why we need this factory interface to be
// generic.
@OptIn(ExperimentalPagingApi::class)
interface RemoteMediatorFactory {
    fun <K : Any, T : Any> create(): RemoteMediator<K, T>?
}
