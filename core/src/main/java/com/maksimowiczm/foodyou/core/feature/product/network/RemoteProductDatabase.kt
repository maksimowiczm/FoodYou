package com.maksimowiczm.foodyou.core.feature.product.network

interface RemoteProductDatabase {
    suspend fun queryAndInsertByName(query: String?)
    suspend fun queryAndInsertByBarcode(barcode: String)
}
