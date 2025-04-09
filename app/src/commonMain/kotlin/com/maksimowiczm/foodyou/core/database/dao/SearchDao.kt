package com.maksimowiczm.foodyou.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maksimowiczm.foodyou.core.database.entity.SearchQueryEntity
import com.maksimowiczm.foodyou.core.database.virtualentity.FoodSearchVirtualEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SearchDao {
    @Query(
        """
        SELECT *
        FROM SearchQueryEntity
        ORDER BY epochSeconds DESC
        LIMIT :limit
    """
    )
    abstract fun observeRecentQueries(limit: Int): Flow<List<SearchQueryEntity>>

    @Upsert
    abstract suspend fun upsert(query: SearchQueryEntity)

    @Query(
        """
        SELECT 
            p.id AS productId,
            p.name AS name,
            p.brand AS brand,
            p.packageWeight AS packageWeight,
            p.servingWeight AS servingWeight,
            p.calories AS calories,
            p.proteins AS proteins,
            p.carbohydrates AS carbohydrates,
            p.sugars AS sugars,
            p.fats AS fats,
            p.saturatedFats AS saturatedFats,
            p.salt AS salt,
            p.sodium AS sodium,
            p.fiber AS fiber
        FROM ProductEntity p
        WHERE 1 = 1
            AND (:query1 IS NULL OR (p.name LIKE '%' || :query1 || '%' OR p.brand LIKE '%' || :query1 || '%'))
            AND (:query2 IS NULL OR (p.name LIKE '%' || :query2 || '%' OR p.brand LIKE '%' || :query2 || '%'))
            AND (:query3 IS NULL OR (p.name LIKE '%' || :query3 || '%' OR p.brand LIKE '%' || :query3 || '%'))
            AND (:query4 IS NULL OR (p.name LIKE '%' || :query4 || '%' OR p.brand LIKE '%' || :query4 || '%'))
            AND (:query5 IS NULL OR (p.name LIKE '%' || :query5 || '%' OR p.brand LIKE '%' || :query5 || '%'))
        """
    )
    abstract fun queryFoodByText(
        query1: String?,
        query2: String?,
        query3: String?,
        query4: String?,
        query5: String?
    ): PagingSource<Int, FoodSearchVirtualEntity>

    @Query(
        """
        SELECT 
            p.id AS productId,
            p.name AS name,
            p.brand AS brand,
            p.packageWeight AS packageWeight,
            p.servingWeight AS servingWeight,
            p.calories AS calories,
            p.proteins AS proteins,
            p.carbohydrates AS carbohydrates,
            p.sugars AS sugars,
            p.fats AS fats,
            p.saturatedFats AS saturatedFats,
            p.salt AS salt,
            p.sodium AS sodium,
            p.fiber AS fiber
        FROM ProductEntity p
        WHERE p.barcode = :barcode
        """
    )
    abstract fun queryFoodByBarcode(barcode: String): PagingSource<Int, FoodSearchVirtualEntity>
}
