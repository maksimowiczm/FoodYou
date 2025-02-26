package com.maksimowiczm.foodyou.network

import androidx.paging.ExperimentalPagingApi

@OptIn(ExperimentalPagingApi::class)
interface ProductRemoteMediatorFactory {
    /**
     * Create a new instance of [ProductRemoteMediator]. If factory can't create a new instance, it
     * should return null.
     *
     * @param query The query to search for.
     *
     * @return A new instance of [ProductRemoteMediator].
     */
    fun createWithQuery(query: String?): ProductRemoteMediator?

    /**
     * Create a new instance of [ProductRemoteMediator]. If factory can't create a new instance, it
     * should return null.
     *
     * @param barcode The barcode to search for.
     *
     * @return A new instance of [ProductRemoteMediator].
     */
    fun createWithBarcode(barcode: String): ProductRemoteMediator?
}
