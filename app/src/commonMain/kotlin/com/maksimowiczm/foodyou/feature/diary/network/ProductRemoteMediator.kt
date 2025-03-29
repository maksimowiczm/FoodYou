package com.maksimowiczm.foodyou.feature.diary.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator

@OptIn(ExperimentalPagingApi::class)
typealias ProductRemoteMediator<T> = RemoteMediator<Int, T>
