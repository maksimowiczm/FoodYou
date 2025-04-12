package com.maksimowiczm.foodyou.core.database.recipe

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class RecipeDao {
    @Query(
        """
        SELECT
            p.*,

            CASE 
                WHEN pm.measurement IS NOT NULL THEN pm.measurement
                WHEN p.servingWeight IS NOT NULL THEN 2
                WHEN p.packageWeight IS NOT NULL THEN 1
                ELSE 0
            END AS measurement,

            CASE 
                WHEN pm.quantity IS NOT NULL THEN pm.quantity
                WHEN p.servingWeight IS NOT NULL THEN 1
                WHEN p.packageWeight IS NOT NULL THEN 1
                ELSE 100
            END AS quantity

        FROM ProductEntity p
        LEFT JOIN ProductMeasurementEntity pm ON p.id = pm.productId
        WHERE 
            :query IS NULL OR (p.name LIKE '%' || :query || '%' OR p.brand LIKE '%' || :query || '%')
            AND (
                pm.createdAt IS NULL OR pm.createdAt = (
                    SELECT MAX(createdAt)
                    FROM ProductMeasurementEntity
                    WHERE productId = pm.productId
                )
            )
        """
    )
    abstract fun observeProductsByText(query: String?): PagingSource<Int, IngredientVirtualEntity>
}
