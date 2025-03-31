package com.maksimowiczm.foodyou.feature.diary.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductSource
import com.maksimowiczm.foodyou.feature.diary.database.entity.OpenFoodFactsPagingKey

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

    @Query(
        """
        DELETE FROM ProductEntity WHERE id IN (
            SELECT product.id FROM ProductEntity AS product
            LEFT JOIN WeightMeasurementEntity AS meal ON meal.productId = product.id 
            WHERE meal.productId IS NULL AND product.productSource = :source
        )
        """
    )
    suspend fun deleteUnusedProducts(source: ProductSource)
}
