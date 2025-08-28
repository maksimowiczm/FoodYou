package com.maksimowiczm.foodyou.business.shared.infrastructure.room.openfoodfacts

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface OpenFoodFactsDao {

    @Query(
        """
        SELECT *
        FROM OpenFoodFactsPagingKey
        WHERE queryString = :query AND country = :country
        """
    )
    suspend fun getPagingKey(query: String, country: String): OpenFoodFactsPagingKeyEntity?

    @Upsert suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKeyEntity)
}
