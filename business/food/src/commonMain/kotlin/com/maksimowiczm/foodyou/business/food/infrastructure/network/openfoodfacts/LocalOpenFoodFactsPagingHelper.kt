package com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts

internal interface LocalOpenFoodFactsPagingHelper {
    suspend fun getPagingKey(query: String, country: String?): OpenFoodFactsPagingKey?

    suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKey)
}
