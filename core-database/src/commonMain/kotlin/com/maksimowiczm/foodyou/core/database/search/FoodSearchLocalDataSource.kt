package com.maksimowiczm.foodyou.core.database.search

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodSearchLocalDataSource {
    @Query(
        """
        WITH UnionSearch AS (
            SELECT
                p.id AS productId,
                NULL AS recipeId,
                p.name AS name,
                p.brand AS brand
            FROM ProductEntity p
            WHERE 
                :query IS NULL OR
                p.name LIKE '%' || :query || '%' OR p.brand LIKE '%' || :query || '%'
            
            UNION ALL
            
            SELECT
                NULL AS productId,
                r.id AS recipeId,
                r.name AS name,
                NULL AS brand
            FROM RecipeEntity r
            WHERE 
                :query IS NULL OR
                r.name LIKE '%' || :query || '%'
        )
        SELECT productId, recipeId
        FROM UnionSearch
        ORDER BY name COLLATE NOCASE, brand COLLATE NOCASE
        LIMIT :limit OFFSET :offset
        """
    )
    fun queryFood(query: String?, limit: Int, offset: Int = 0): Flow<List<FoodSearchEntity>>

    @Query(
        """
        SELECT
            p.id AS productId,
            NULL AS recipeId
        FROM ProductEntity p
        WHERE p.barcode = :barcode
        ORDER BY name COLLATE NOCASE, brand COLLATE NOCASE
        LIMIT :limit OFFSET :offset
        """
    )
    fun queryFoodByBarcode(
        barcode: String,
        limit: Int,
        offset: Int = 0
    ): Flow<List<FoodSearchEntity>>
}
