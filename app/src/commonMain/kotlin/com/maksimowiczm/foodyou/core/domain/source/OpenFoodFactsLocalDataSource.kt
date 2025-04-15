package com.maksimowiczm.foodyou.core.domain.source

import com.maksimowiczm.foodyou.core.data.model.openfoodfacts.OpenFoodFactsPagingKeyEntity

interface OpenFoodFactsLocalDataSource {
    suspend fun getPagingKey(query: String, country: String): OpenFoodFactsPagingKeyEntity?
    suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKeyEntity)
    suspend fun clearPagingKeys()
}
