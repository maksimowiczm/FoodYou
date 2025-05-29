package com.maksimowiczm.foodyou.core.data.database.food

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import com.maksimowiczm.foodyou.core.data.model.food.FoodSearchEntity
import com.maksimowiczm.foodyou.core.domain.source.FoodLocalDataSource

@Dao
abstract class FoodDao : FoodLocalDataSource {
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        WITH
        FilteredData AS (
            SELECT
                s.productId,
                s.recipeId,
                :epochDay AS epochDay,
                :mealId AS mealId,
                s.name,
                s.brand,
                s.barcode,
                s.calories,
                s.proteins,
                s.carbohydrates,
                s.fats,
                s.packageWeight,
                s.servingWeight,
                NULL AS measurementId,
                s.measurement,
                s.quantity,
                0 AS isMeasured
            FROM MeasurementSuggestionView s
            WHERE (:query IS NULL OR (s.name LIKE '%' || :query || '%' OR s.brand LIKE '%' || :query || '%'))
            
            UNION ALL
            
            SELECT 
                m.productId,
                m.recipeId,
                m.epochDay,
                m.mealId,
                m.name,
                m.brand,
                m.barcode,
                m.calories,
                m.proteins,
                m.carbohydrates,
                m.fats,
                m.packageWeight,
                m.servingWeight,
                m.measurementId,
                m.measurement,
                m.quantity,
                1 AS isMeasured
            FROM MeasuredFoodView m
            WHERE 
                m.mealId = :mealId
                AND m.epochDay = :epochDay
                AND (:query IS NULL OR (m.name LIKE '%' || :query || '%' OR m.brand LIKE '%' || :query || '%'))
        ),
        
        -- Get min measurement ID for each product/recipe with separate columns
        MinMeasurements AS (
            SELECT
                productId,
                recipeId,
                MIN(measurementId) AS minMeasurementId
            FROM FilteredData
            WHERE measurementId IS NOT NULL
            GROUP BY productId, recipeId
        ),
        
        -- Remove suggestions that have measured counterparts
        FinalData AS (
            SELECT fd.*
            FROM FilteredData fd
            WHERE 
                fd.isMeasured = 1 
                OR NOT EXISTS (
                    SELECT 1 
                    FROM FilteredData m 
                    WHERE 
                        m.isMeasured = 1
                        AND ((fd.productId IS NOT NULL AND fd.productId = m.productId) 
                        OR (fd.recipeId IS NOT NULL AND fd.recipeId = m.recipeId))
                )
        )
        
        SELECT 
            fd.*,
            CASE 
                WHEN fd.measurementId IS NULL THEN 
                    CASE 
                        WHEN fd.productId IS NOT NULL THEN 'p' || fd.productId 
                        ELSE 'r' || fd.recipeId
                    END
                WHEN fd.measurementId = mm.minMeasurementId THEN
                    CASE 
                        WHEN fd.productId IS NOT NULL THEN 'p' || fd.productId 
                        ELSE 'r' || fd.recipeId
                    END
                ELSE
                    CASE 
                        WHEN fd.productId IS NOT NULL THEN 'p' || fd.productId || '-' || fd.measurementId
                        ELSE 'r' || fd.recipeId || '-' || fd.measurementId
                    END
            END AS uiId
        FROM FinalData fd
        LEFT JOIN MinMeasurements mm ON
            (fd.productId IS NOT NULL AND fd.productId = mm.productId AND mm.recipeId IS NULL) OR
            (fd.recipeId IS NOT NULL AND fd.recipeId = mm.recipeId AND mm.productId IS NULL)
        ORDER BY fd.name COLLATE NOCASE, fd.brand COLLATE NOCASE
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
        FilteredData AS (
            SELECT
                s.productId,
                s.recipeId,
                :epochDay AS epochDay,
                :mealId AS mealId,
                s.name,
                s.brand,
                s.barcode,
                s.calories,
                s.proteins,
                s.carbohydrates,
                s.fats,
                s.packageWeight,
                s.servingWeight,
                NULL AS measurementId,
                s.measurement,
                s.quantity,
                0 AS isMeasured
            FROM MeasurementSuggestionView s
            WHERE barcode = :barcode
            
            UNION ALL
            
            SELECT 
                m.productId,
                m.recipeId,
                m.epochDay,
                m.mealId,
                m.name,
                m.brand,
                m.barcode,
                m.calories,
                m.proteins,
                m.carbohydrates,
                m.fats,
                m.packageWeight,
                m.servingWeight,
                m.measurementId,
                m.measurement,
                m.quantity,
                1 AS isMeasured
            FROM MeasuredFoodView m
            WHERE 
                m.mealId = :mealId
                AND m.epochDay = :epochDay
                AND m.barcode = :barcode
        ),
        
        -- Get min measurement ID for each product/recipe with separate columns
        MinMeasurements AS (
            SELECT
                productId,
                recipeId,
                MIN(measurementId) AS minMeasurementId
            FROM FilteredData
            WHERE measurementId IS NOT NULL
            GROUP BY productId, recipeId
        ),
        
        -- Remove suggestions that have measured counterparts
        FinalData AS (
            SELECT fd.*
            FROM FilteredData fd
            WHERE 
                fd.isMeasured = 1 
                OR NOT EXISTS (
                    SELECT 1 
                    FROM FilteredData m 
                    WHERE 
                        m.isMeasured = 1
                        AND ((fd.productId IS NOT NULL AND fd.productId = m.productId) 
                        OR (fd.recipeId IS NOT NULL AND fd.recipeId = m.recipeId))
                )
        )
        
        SELECT 
            fd.*,
            CASE 
                WHEN fd.measurementId IS NULL THEN 
                    CASE 
                        WHEN fd.productId IS NOT NULL THEN 'p' || fd.productId 
                        ELSE 'r' || fd.recipeId
                    END
                WHEN fd.measurementId = mm.minMeasurementId THEN
                    CASE 
                        WHEN fd.productId IS NOT NULL THEN 'p' || fd.productId 
                        ELSE 'r' || fd.recipeId
                    END
                ELSE
                    CASE 
                        WHEN fd.productId IS NOT NULL THEN 'p' || fd.productId || '-' || fd.measurementId
                        ELSE 'r' || fd.recipeId || '-' || fd.measurementId
                    END
            END AS uiId
        FROM FinalData fd
        LEFT JOIN MinMeasurements mm ON
            (fd.productId IS NOT NULL AND fd.productId = mm.productId AND mm.recipeId IS NULL) OR
            (fd.recipeId IS NOT NULL AND fd.recipeId = mm.recipeId AND mm.productId IS NULL)
        ORDER BY fd.name COLLATE NOCASE, fd.brand COLLATE NOCASE
        """
    )
    abstract override fun queryFoodByBarcode(
        barcode: String,
        mealId: Long,
        epochDay: Int
    ): PagingSource<Int, FoodSearchEntity>
}
