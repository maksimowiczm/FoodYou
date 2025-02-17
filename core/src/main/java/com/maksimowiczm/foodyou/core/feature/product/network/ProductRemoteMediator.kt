package com.maksimowiczm.foodyou.core.feature.product.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.core.feature.product.database.ProductEntity

@OptIn(ExperimentalPagingApi::class)
typealias ProductRemoteMediator = RemoteMediator<Int, ProductEntity>

@OptIn(ExperimentalPagingApi::class)
fun interface ProductRemoteMediatorFactory {
    /**
     * Create a new instance of [ProductRemoteMediator]. If factory can't create a new instance, it
     * should return null.
     *
     * @param query The query to search for.
     *
     * @return A new instance of [ProductRemoteMediator].
     */
    fun create(query: String?): ProductRemoteMediator?
}
