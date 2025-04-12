package com.maksimowiczm.foodyou.core.database.search

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
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
        WITH
        Suggestion AS (
            SELECT *
            FROM MeasurementSuggestionView s
            WHERE 1 = 1
                AND (:query1 IS NULL OR (name LIKE '%' || :query1 || '%' OR brand LIKE '%' || :query1 || '%'))
                AND (:query2 IS NULL OR (name LIKE '%' || :query2 || '%' OR brand LIKE '%' || :query2 || '%'))
                AND (:query3 IS NULL OR (name LIKE '%' || :query3 || '%' OR brand LIKE '%' || :query3 || '%'))
                AND (:query4 IS NULL OR (name LIKE '%' || :query4 || '%' OR brand LIKE '%' || :query4 || '%'))
                AND (:query5 IS NULL OR (name LIKE '%' || :query5 || '%' OR brand LIKE '%' || :query5 || '%'))
        ),
        Measured AS (
            SELECT *
            FROM MeasuredFoodView
            WHERE 
                mealId = :mealId
                AND epochDay = :epochDay
                AND (:query1 IS NULL OR (name LIKE '%' || :query1 || '%' OR brand LIKE '%' || :query1 || '%'))
                AND (:query2 IS NULL OR (name LIKE '%' || :query2 || '%' OR brand LIKE '%' || :query2 || '%'))
                AND (:query3 IS NULL OR (name LIKE '%' || :query3 || '%' OR brand LIKE '%' || :query3 || '%'))
                AND (:query4 IS NULL OR (name LIKE '%' || :query4 || '%' OR brand LIKE '%' || :query4 || '%'))
                AND (:query5 IS NULL OR (name LIKE '%' || :query5 || '%' OR brand LIKE '%' || :query5 || '%'))
        )
        SELECT * FROM Measured
        UNION
        SELECT
            s.productId AS productId,
            s.recipeId AS recipeId,
            :epochDay AS epochDay,
            :mealId AS mealId,
            s.name AS name,
            s.brand AS brand,
            s.barcode AS barcode,
            s.calories AS calories,
            s.proteins AS proteins,
            s.carbohydrates AS carbohydrates,
            s.fats AS fats,
            s.packageWeight AS packageWeight,
            s.servingWeight AS servingWeight,
            NULL AS measurementId,
            s.measurement AS measurement,
            s.quantity AS quantity
        FROM Suggestion s
        WHERE s.productId NOT IN (
            SELECT productId
            FROM Measured
        )
        """
    )
    abstract fun queryFood(
        query1: String?,
        query2: String?,
        query3: String?,
        query4: String?,
        query5: String?,
        mealId: Long,
        epochDay: Int
    ): PagingSource<Int, FoodSearchVirtualEntity>

    @Query(
        """
        WITH
        Suggestion AS (
            SELECT *
            FROM MeasurementSuggestionView s
            WHERE s.barcode = :barcode
        ),
        Measured AS (
            SELECT *
            FROM MeasuredFoodView
            WHERE 
                mealId = :mealId
                AND epochDay = :epochDay
                AND barcode = :barcode
        )
        SELECT * FROM Measured
        UNION
        SELECT
            s.productId AS productId,
            NULL AS recipeId,
            :epochDay AS epochDay,
            :mealId AS mealId,
            s.name AS name,
            s.brand AS brand,
            s.barcode AS barcode,
            s.calories AS calories,
            s.proteins AS proteins,
            s.carbohydrates AS carbohydrates,
            s.fats AS fats,
            s.packageWeight AS packageWeight,
            s.servingWeight AS servingWeight,
            NULL AS measurementId,
            s.measurement AS measurement,
            s.quantity AS quantity
        FROM Suggestion s
        WHERE s.productId NOT IN (
            SELECT productId
            FROM Measured
        )
        """
    )
    abstract fun queryFoodByBarcode(
        barcode: String,
        mealId: Long,
        epochDay: Int
    ): PagingSource<Int, FoodSearchVirtualEntity>
}
