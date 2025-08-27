package com.maksimowiczm.foodyou.business.food.infrastructure.network.usda

internal interface LocalUsdaPagingHelper {
    suspend fun getPagingKey(query: String): USDAPagingKey?

    suspend fun upsertPagingKey(pagingKey: USDAPagingKey)
}
