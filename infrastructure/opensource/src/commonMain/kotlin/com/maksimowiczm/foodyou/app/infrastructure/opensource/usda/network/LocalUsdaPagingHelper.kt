package com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network

internal interface LocalUsdaPagingHelper {
    suspend fun getPagingKey(query: String): USDAPagingKey?

    suspend fun upsertPagingKey(pagingKey: USDAPagingKey)
}
