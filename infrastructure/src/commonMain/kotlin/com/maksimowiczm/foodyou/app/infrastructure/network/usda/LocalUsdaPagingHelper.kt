package com.maksimowiczm.foodyou.app.infrastructure.network.usda

internal interface LocalUsdaPagingHelper {
    suspend fun getPagingKey(query: String): USDAPagingKey?

    suspend fun upsertPagingKey(pagingKey: USDAPagingKey)
}
