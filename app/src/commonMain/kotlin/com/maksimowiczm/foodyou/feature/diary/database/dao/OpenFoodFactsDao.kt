package com.maksimowiczm.foodyou.feature.diary.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.maksimowiczm.foodyou.feature.diary.database.entity.OpenFoodFactsPagingKey

@Dao
interface OpenFoodFactsDao {
    @Query(
        """
        SELECT * 
        FROM OpenFoodFactsPagingKey 
        WHERE queryString = :query
        AND (:country IS NULL OR country = :country)
        """
    )
    suspend fun getPagingKey(query: String, country: String?): OpenFoodFactsPagingKey?

    @Upsert
    suspend fun update(pagingKey: OpenFoodFactsPagingKey)

    @Transaction
    suspend fun updatePagingKey(pagingKey: OpenFoodFactsPagingKey) {
        val existingKey = getPagingKey(
            query = pagingKey.queryString,
            country = pagingKey.country
        )

        if (existingKey == null) {
            update(pagingKey)
        } else {
            update(
                existingKey.copy(
                    fetchedCount = pagingKey.fetchedCount,
                    totalCount = pagingKey.totalCount
                )
            )
        }
    }

    @Query(
        """
        DELETE FROM OpenFoodFactsPagingKey 
        """
    )
    suspend fun clearPagingKeys()
}
