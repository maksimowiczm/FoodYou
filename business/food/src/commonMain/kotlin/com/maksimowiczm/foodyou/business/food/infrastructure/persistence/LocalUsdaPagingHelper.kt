package com.maksimowiczm.foodyou.business.food.infrastructure.persistence

import com.maksimowiczm.foodyou.business.food.domain.USDAPagingKey

internal interface LocalUsdaPagingHelper {
    suspend fun getPagingKey(query: String): USDAPagingKey?

    suspend fun upsertPagingKey(pagingKey: USDAPagingKey)
}
