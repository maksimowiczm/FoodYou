package com.maksimowiczm.foodyou.core.feature.product.network

interface ProductsRemoteDatabase {
    suspend fun queryAndInsertByName(query: String?, limit: Int)
    suspend fun queryAndInsertByBarcode(barcode: String)
}
