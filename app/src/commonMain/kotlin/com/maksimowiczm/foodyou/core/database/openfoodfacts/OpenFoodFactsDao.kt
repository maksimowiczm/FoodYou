package com.maksimowiczm.foodyou.core.database.openfoodfacts

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
abstract class OpenFoodFactsDao {
    @Query(
        """
        SELECT * 
        FROM OpenFoodFactsPagingKeyEntity 
        WHERE queryString = :query
            AND country = :country
        """
    )
    abstract suspend fun getPagingKey(query: String, country: String): OpenFoodFactsPagingKeyEntity?

    @Upsert
    abstract suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKeyEntity)

    @Query(
        """
        DELETE FROM OpenFoodFactsPagingKeyEntity 
        """
    )
    abstract suspend fun clearPagingKeys()
}
