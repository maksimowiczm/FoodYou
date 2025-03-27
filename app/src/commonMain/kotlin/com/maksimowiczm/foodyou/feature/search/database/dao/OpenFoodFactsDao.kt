package com.maksimowiczm.foodyou.feature.search.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maksimowiczm.foodyou.feature.search.database.entity.OpenFoodFactsPagingKeyEntity

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
    suspend fun getPagingKey(query: String, country: String): OpenFoodFactsPagingKeyEntity?

    @Upsert
    suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKeyEntity)

    @Query(
        """
        DELETE FROM OpenFoodFactsPagingKey 
        """
    )
    suspend fun clearPagingKeys()
}
