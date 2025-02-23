package com.maksimowiczm.foodyou.feature.addfood.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.feature.openfoodfacts.database.ProductEntity

@OptIn(ExperimentalPagingApi::class)
typealias ProductRemoteMediator = RemoteMediator<Int, ProductEntity>
