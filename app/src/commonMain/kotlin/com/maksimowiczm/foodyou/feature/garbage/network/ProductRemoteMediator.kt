package com.maksimowiczm.foodyou.feature.garbage.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.feature.garbage.database.entity.ProductEntity

@OptIn(ExperimentalPagingApi::class)
typealias ProductRemoteMediator = RemoteMediator<Int, ProductEntity>
