package com.maksimowiczm.foodyou.core.domain.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator

// You probably wonder why tf is this generic but you should probably wonder why paging library
// enforces you to define type for the remote mediator.
//
// Probably they know why... But I don't.
//
// Product mediator must not have any type restrictions because it will be used with custom search
// entities which will allow for searching for products together with recipes and other entities
// (all in one paging source).
@OptIn(ExperimentalPagingApi::class)
typealias ProductRemoteMediator<T> = RemoteMediator<Int, T>

@OptIn(ExperimentalPagingApi::class)
interface ProductNetworkDataSource {
    /**
     * Create a new instance of [ProductRemoteMediator]. If factory can't create a new instance, it
     * should return null.
     *
     * @param query The query to search for.
     *
     * @return A new instance of [ProductRemoteMediator].
     */
    fun <T : Any> createRemoteMediatorWithQuery(query: String?): ProductRemoteMediator<T>?

    /**
     * Create a new instance of [ProductRemoteMediator]. If factory can't create a new instance, it
     * should return null.
     *
     * @param barcode The barcode to search for.
     *
     * @return A new instance of [ProductRemoteMediator].
     */
    fun <T : Any> createRemoteMediatorWithBarcode(barcode: String): ProductRemoteMediator<T>?
}
