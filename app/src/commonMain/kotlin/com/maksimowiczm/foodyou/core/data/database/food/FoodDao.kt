package com.maksimowiczm.foodyou.core.data.database.food

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import com.maksimowiczm.foodyou.core.data.model.food.FoodSearchEntity
import com.maksimowiczm.foodyou.core.domain.source.FoodLocalDataSource

// TODO: bro this is so slow

@Dao
abstract class FoodDao : FoodLocalDataSource {
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        WITH
        Suggestion AS (
            SELECT *
            FROM MeasurementSuggestionView s
            WHERE :query IS NULL OR (name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%')
        ),
        Measured AS (
            SELECT *
            FROM MeasuredFoodView
            WHERE 
                mealId = :mealId
                AND epochDay = :epochDay
                AND :query IS NULL OR (name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%')
        ),
        Intermediate AS (
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
        ),
        Counted AS (
            SELECT 
                productId AS productId,
                recipeId AS recipeId,
                COUNT(*) AS count
            FROM Intermediate
            GROUP BY productId, recipeId
        ),
        IntermediateWithMin AS (
        -- Can't use window function when API < 30
--                SELECT *,
--                       CASE 
--                           WHEN measurementId IS NULL THEN NULL
--                           WHEN measurementId = MIN(measurementId) OVER (
--                               PARTITION BY 
--                                   CASE 
--                                       WHEN productId IS NOT NULL THEN productId
--                                       ELSE recipeId
--                                   END
--                           ) THEN 1
--                           ELSE 0
--                       END AS isLowestMeasurementId
--                FROM Intermediate
            WITH GroupMinValues AS (
            SELECT 
                CASE 
                    WHEN productId IS NOT NULL THEN productId
                    ELSE recipeId
                END AS groupId,
                MIN(measurementId) AS minMeasurementId
            FROM Intermediate
            GROUP BY 
                CASE 
                    WHEN productId IS NOT NULL THEN productId
                    ELSE recipeId
                END
        )
        SELECT i.*,
            CASE 
                WHEN i.measurementId IS NULL THEN NULL
                WHEN i.measurementId = g.minMeasurementId THEN 1
                ELSE 0
            END AS isLowestMeasurementId
        FROM Intermediate i
        LEFT JOIN GroupMinValues g ON 
            (i.productId IS NOT NULL AND i.productId = g.groupId) OR
            (i.productId IS NULL AND i.recipeId = g.groupId)
        )
        SELECT 
            i.*,
            CASE 
                WHEN i.productId IS NOT NULL AND i.isLowestMeasurementId IS NULL THEN i.productId
                WHEN i.recipeId IS NOT NULL AND i.isLowestMeasurementId IS NULL THEN i.recipeId
                WHEN i.productId IS NOT NULL AND i.isLowestMeasurementId = 1 THEN i.productId
                WHEN i.recipeId IS NOT NULL AND i.isLowestMeasurementId = 1 THEN i.recipeId
                WHEN i.productId IS NOT NULL AND i.isLowestMeasurementId = 0 THEN i.productId || "-" || i.measurementId
                WHEN i.recipeId IS NOT NULL AND i.isLowestMeasurementId = 0 THEN i.recipeId || "-" || i.measurementId
                ELSE NULL
            END AS uiId
        FROM IntermediateWithMin i
        JOIN Counted c 
            ON (i.productId IS NULL AND i.recipeId = c.recipeId) 
            OR (i.productId = c.productId AND i.recipeId IS NULL)
        ORDER BY name COLLATE NOCASE, brand COLLATE NOCASE
        """
    )
    abstract override fun queryFood(
        query: String?,
        mealId: Long,
        epochDay: Int
    ): PagingSource<Int, FoodSearchEntity>

    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
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
        ),
        Intermediate AS (
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
        ),
        Counted AS (
            SELECT 
                productId AS productId,
                recipeId AS recipeId,
                COUNT(*) AS count
            FROM Intermediate
            GROUP BY productId, recipeId
        ),
        IntermediateWithMin AS (
            WITH GroupMinValues AS (
            SELECT 
                CASE 
                    WHEN productId IS NOT NULL THEN productId
                    ELSE recipeId
                END AS groupId,
                MIN(measurementId) AS minMeasurementId
            FROM Intermediate
            GROUP BY 
                CASE 
                    WHEN productId IS NOT NULL THEN productId
                    ELSE recipeId
                END
        )
        SELECT i.*,
            CASE 
                WHEN i.measurementId IS NULL THEN NULL
                WHEN i.measurementId = g.minMeasurementId THEN 1
                ELSE 0
            END AS isLowestMeasurementId
        FROM Intermediate i
        LEFT JOIN GroupMinValues g ON 
            (i.productId IS NOT NULL AND i.productId = g.groupId) OR
            (i.productId IS NULL AND i.recipeId = g.groupId)
        )
        SELECT 
            i.*,
            CASE 
                WHEN i.productId IS NOT NULL AND i.isLowestMeasurementId IS NULL THEN i.productId
                WHEN i.recipeId IS NOT NULL AND i.isLowestMeasurementId IS NULL THEN i.recipeId
                WHEN i.productId IS NOT NULL AND i.isLowestMeasurementId = 1 THEN i.productId
                WHEN i.recipeId IS NOT NULL AND i.isLowestMeasurementId = 1 THEN i.recipeId
                WHEN i.productId IS NOT NULL AND i.isLowestMeasurementId = 0 THEN i.productId || "-" || i.measurementId
                WHEN i.recipeId IS NOT NULL AND i.isLowestMeasurementId = 0 THEN i.recipeId || "-" || i.measurementId
                ELSE NULL
            END AS uiId
        FROM IntermediateWithMin i
        JOIN Counted c 
            ON (i.productId IS NULL AND i.recipeId = c.recipeId) 
            OR (i.productId = c.productId AND i.recipeId IS NULL)
        ORDER BY name COLLATE NOCASE, brand COLLATE NOCASE
        """
    )
    abstract override fun queryFoodByBarcode(
        barcode: String,
        mealId: Long,
        epochDay: Int
    ): PagingSource<Int, FoodSearchEntity>
}
