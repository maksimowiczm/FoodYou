package com.maksimowiczm.foodyou.feature.diary.database.openfoodfacts

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
abstract class OpenFoodFactsDao {
    @Query(
        """
        SELECT * 
        FROM OpenFoodFactsPagingKey 
        WHERE queryString = :query
            AND country = :country
        """
    )
    abstract suspend fun getPagingKey(query: String, country: String): OpenFoodFactsPagingKey?

    @Upsert
    abstract suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKey)

    @Query(
        """
        DELETE FROM OpenFoodFactsPagingKey 
        """
    )
    abstract suspend fun clearPagingKeys()
}
