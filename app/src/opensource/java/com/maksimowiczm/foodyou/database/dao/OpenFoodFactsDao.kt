package com.maksimowiczm.foodyou.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maksimowiczm.foodyou.database.entity.OpenFoodFactsPagingKey

@Dao
interface OpenFoodFactsDao {
    @Query(
        """
        SELECT * 
        FROM OpenFoodFactsPagingKey 
        WHERE queryString = :query
            AND country = :country
        """
    )
    suspend fun getPagingKey(query: String, country: String): OpenFoodFactsPagingKey?

    @Upsert
    suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKey)

    @Query(
        """
        DELETE FROM OpenFoodFactsPagingKey 
        """
    )
    suspend fun clearPagingKeys()
}
