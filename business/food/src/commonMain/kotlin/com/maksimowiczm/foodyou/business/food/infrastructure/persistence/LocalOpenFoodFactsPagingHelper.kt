package com.maksimowiczm.foodyou.business.food.infrastructure.persistence

import com.maksimowiczm.foodyou.business.food.domain.OpenFoodFactsPagingKey

internal interface LocalOpenFoodFactsPagingHelper {
    suspend fun getPagingKey(query: String, country: String?): OpenFoodFactsPagingKey?

    suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKey)
}
