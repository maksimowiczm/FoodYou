package com.maksimowiczm.foodyou.core.data.database.food

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.maksimowiczm.foodyou.core.data.model.food.FoodSearchEntity
import com.maksimowiczm.foodyou.core.domain.source.FoodLocalDataSource

@Dao
abstract class FoodDao : FoodLocalDataSource {
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
        WHERE NOT EXISTS (
            SELECT 1
            FROM Measured m
            WHERE 
                (m.productId IS NOT NULL AND m.productId = s.productId)
                OR (m.recipeId IS NOT NULL AND m.recipeId = s.recipeId)
        )
        ORDER BY name COLLATE NOCASE, brand COLLATE NOCASE
        """
    )
    abstract override fun queryFood(
        query1: String?,
        query2: String?,
        query3: String?,
        query4: String?,
        query5: String?,
        mealId: Long,
        epochDay: Int
    ): PagingSource<Int, FoodSearchEntity>

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
        ORDER BY name COLLATE NOCASE, brand COLLATE NOCASE
        """
    )
    abstract override fun queryFoodByBarcode(
        barcode: String,
        mealId: Long,
        epochDay: Int
    ): PagingSource<Int, FoodSearchEntity>
}
