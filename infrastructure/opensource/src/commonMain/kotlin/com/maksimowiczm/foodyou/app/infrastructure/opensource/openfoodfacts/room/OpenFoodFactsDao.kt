package com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.room

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
