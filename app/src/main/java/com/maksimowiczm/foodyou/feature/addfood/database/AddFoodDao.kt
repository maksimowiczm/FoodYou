package com.maksimowiczm.foodyou.feature.addfood.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.maksimowiczm.foodyou.feature.product.database.ProductEntity

@Dao
interface AddFoodDao {
    @Query(
        """
        WITH Measurements AS (
            SELECT wm.*
            FROM WeightMeasurementEntity wm
            WHERE
                (wm.mealId = :mealId AND wm.diaryEpochDay = :epochDay AND isDeleted = 0)
                OR wm.id IN (
                    SELECT wm2.id
                    FROM WeightMeasurementEntity wm2
                    WHERE wm2.productId = wm.productId
                    AND (wm2.mealId = :mealId OR wm2.diaryEpochDay = :epochDay AND wm2.isDeleted = 1)
                    ORDER BY wm2.createdAt DESC
                    LIMIT 1
                )
                OR wm.id IN (
                    SELECT wm2.id
                    FROM WeightMeasurementEntity wm2
                    WHERE wm2.productId = wm.productId
                    AND (wm2.mealId != :mealId OR wm2.diaryEpochDay != :epochDay)
                    ORDER BY wm2.createdAt DESC
                    LIMIT 1
                )
        )
        SELECT
            p.id AS p_id,
            p.name AS p_name,
            p.brand AS p_brand,
            p.barcode AS p_barcode,
            p.calories AS p_calories,
            p.packageWeight AS p_packageWeight,
            p.servingWeight AS p_servingWeight,
            p.weightUnit AS p_weightUnit,
            m.id AS wm_id,
            m.quantity AS wm_quantity,
            m.measurement AS wm_measurement,
            m.createdAt AS wm_createdAt,
            CASE 
                WHEN 
                m.productId IS NOT NULL 
                AND m.isDeleted = 0
                AND m.mealId = :mealId
                AND m.diaryEpochDay = :epochDay
                THEN 1
                ELSE 0 
            END AS hasMeasurement
        FROM ProductEntity p
        LEFT JOIN Measurements m ON p.id = m.productId
        WHERE (:query IS NULL OR p.name LIKE '%' || :query || '%' OR p.brand LIKE '%' || :query || '%')
        ORDER BY hasMeasurement DESC, m.createdAt DESC
        LIMIT :limit
        """
    )
    /**
     * Get all products and all of their measurements for the given meal and epoch day. If product
     * is not measured for the given meal and epoch day, get the latest measurement for the product.
     * If product is not measured at all, return null for the measurement.
     *
     * If measurement is in given meal and epoch day, hasMeasurement is set to 1, otherwise 0.
     */
    suspend fun getProductsWithMeasurementByQuery(
        mealId: Long,
        epochDay: Long,
        query: String?,
        limit: Int
    ): List<ProductSearchEntity>

    @Query(
        """
        WITH Measurements AS (
            SELECT wm.*
            FROM WeightMeasurementEntity wm
            WHERE
                (wm.mealId = :mealId AND wm.diaryEpochDay = :epochDay AND isDeleted = 0)
                OR wm.id IN (
                    SELECT wm2.id
                    FROM WeightMeasurementEntity wm2
                    WHERE wm2.productId = wm.productId
                    AND (wm2.mealId = :mealId OR wm2.diaryEpochDay = :epochDay AND wm2.isDeleted = 1)
                    ORDER BY wm2.createdAt DESC
                    LIMIT 1
                )
                OR wm.id IN (
                    SELECT wm2.id
                    FROM WeightMeasurementEntity wm2
                    WHERE wm2.productId = wm.productId
                    AND (wm2.mealId != :mealId OR wm2.diaryEpochDay != :epochDay)
                    ORDER BY wm2.createdAt DESC
                    LIMIT 1
                )
        )
        SELECT
            p.id AS p_id,
            p.name AS p_name,
            p.brand AS p_brand,
            p.barcode AS p_barcode,
            p.calories AS p_calories,
            p.packageWeight AS p_packageWeight,
            p.servingWeight AS p_servingWeight,
            p.weightUnit AS p_weightUnit,
            m.id AS wm_id,
            m.quantity AS wm_quantity,
            m.measurement AS wm_measurement,
            m.createdAt AS wm_createdAt,
            CASE 
                WHEN 
                m.productId IS NOT NULL 
                AND m.isDeleted = 0
                AND m.mealId = :mealId
                AND m.diaryEpochDay = :epochDay
                THEN 1
                ELSE 0 
            END AS hasMeasurement
        FROM ProductEntity p
        LEFT JOIN Measurements m ON p.id = m.productId
        WHERE p.barcode = :barcode
        ORDER BY hasMeasurement DESC, m.createdAt DESC
        LIMIT :limit
        """
    )
    suspend fun getProductsWithMeasurementByBarcode(
        mealId: Long,
        epochDay: Long,
        barcode: String,
        limit: Int
    ): List<ProductSearchEntity>

    @Query(
        """
        SELECT *
        FROM WeightMeasurementEntity
        WHERE isDeleted = 0
        AND id = :portionId
        """
    )
    suspend fun getWeightMeasurement(portionId: Long): WeightMeasurementEntity?

    @Insert
    suspend fun insertWeightMeasurement(weightMeasurement: WeightMeasurementEntity): Long

    @Query(
        """
        UPDATE WeightMeasurementEntity
        SET isDeleted = 1
        WHERE id = :id
        """
    )
    suspend fun deleteWeightMeasurement(id: Long)

    @Query(
        """
        SELECT measurement, quantity
        FROM WeightMeasurementEntity
        WHERE productId = :productId
        GROUP BY measurement
        ORDER BY MAX(createdAt) DESC
        """
    )
    suspend fun getQuantitySuggestionsByProductId(productId: Long): List<QuantitySuggestionEntity>
}
