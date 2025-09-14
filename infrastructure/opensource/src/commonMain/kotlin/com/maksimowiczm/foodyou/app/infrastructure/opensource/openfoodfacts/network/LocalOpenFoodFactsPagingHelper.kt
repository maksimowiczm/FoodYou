package com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network

internal interface LocalOpenFoodFactsPagingHelper {
    suspend fun getPagingKey(query: String, country: String?): OpenFoodFactsPagingKey?

    suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKey)
}
