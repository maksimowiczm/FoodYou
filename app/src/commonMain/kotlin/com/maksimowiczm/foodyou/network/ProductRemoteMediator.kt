package com.maksimowiczm.foodyou.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.database.entity.ProductEntity

@OptIn(ExperimentalPagingApi::class)
typealias ProductRemoteMediator = RemoteMediator<Int, ProductEntity>
