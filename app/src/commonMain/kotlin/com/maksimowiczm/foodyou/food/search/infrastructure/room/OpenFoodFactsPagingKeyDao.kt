package com.maksimowiczm.foodyou.food.search.infrastructure.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface OpenFoodFactsPagingKeyDao {

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
