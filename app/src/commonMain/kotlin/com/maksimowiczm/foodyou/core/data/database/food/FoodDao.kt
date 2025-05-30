package com.maksimowiczm.foodyou.core.data.database.food

import androidx.room.Dao
import androidx.room.Query
import com.maksimowiczm.foodyou.core.data.model.food.FoodSearchEntity
import com.maksimowiczm.foodyou.core.domain.source.FoodLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FoodDao : FoodLocalDataSource {
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
    abstract override fun queryFood(
        query: String?,
        limit: Int,
        offset: Int
    ): Flow<List<FoodSearchEntity>>

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
    abstract override fun queryFoodByBarcode(
        barcode: String,
        limit: Int,
        offset: Int
    ): Flow<List<FoodSearchEntity>>
}
