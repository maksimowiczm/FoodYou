package com.maksimowiczm.foodyou.feature.search.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductEntity

@OptIn(ExperimentalPagingApi::class)
typealias ProductRemoteMediator = RemoteMediator<Int, ProductEntity>
