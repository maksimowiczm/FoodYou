package com.maksimowiczm.foodyou.core.database.measurement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.maksimowiczm.foodyou.core.database.measurement.FoodMeasurementVirtualEntity
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.GRAM
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.PACKAGE
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.SERVING
import com.maksimowiczm.foodyou.core.database.measurement.SuggestionVirtualEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MeasurementDao {
    @Insert
    abstract suspend fun addProductMeasurement(entity: ProductMeasurementEntity)

    @Update
    abstract suspend fun updateProductMeasurement(entity: ProductMeasurementEntity)

    @Query(
        """
        SELECT *
        FROM ProductMeasurementEntity
        WHERE id = :id
        AND isDeleted = 0
        """
    )
    abstract suspend fun getProductMeasurement(id: Long): ProductMeasurementEntity?

    @Query(
        """
        UPDATE ProductMeasurementEntity
        SET isDeleted = 1
        WHERE id = :id
        """
    )
    abstract suspend fun deleteProductMeasurement(id: Long)

    @Query(
        """
        UPDATE ProductMeasurementEntity
        SET isDeleted = 0
        WHERE id = :id
        """
    )
    abstract suspend fun restoreProductMeasurement(id: Long)

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
            p.fiber AS fiber,
            m.id AS measurementId,
            m.measurement AS measurement,
            m.quantity AS quantity
        FROM ProductMeasurementEntity m
        LEFT JOIN ProductEntity p ON p.id = m.productId
        WHERE diaryEpochDay = :epochDay
        AND mealId = :mealId
        AND isDeleted = 0
        ORDER BY m.createdAt DESC
        """
    )
    abstract fun observeMeasurements(
        epochDay: Int,
        mealId: Long
    ): Flow<List<FoodMeasurementVirtualEntity>>

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
            p.fiber AS fiber,
            m.id AS measurementId,
            m.measurement AS measurement,
            m.quantity AS quantity
        FROM ProductMeasurementEntity m
        LEFT JOIN ProductEntity p ON p.id = m.productId
        WHERE m.id = :measurementId
        AND m.isDeleted = 0
        """
    )
    abstract fun observeMeasurement(measurementId: Long): Flow<FoodMeasurementVirtualEntity?>

    @Query(
        """
        SELECT 
            COALESCE(m.quantity,
                CASE 
                    WHEN p.servingWeight IS NOT NULL THEN 1
                    WHEN p.packageWeight IS NOT NULL THEN 1
                    ELSE 100
                END
            ) AS quantity,
            COALESCE(m.measurement,
                CASE 
                    WHEN p.servingWeight IS NOT NULL THEN $SERVING
                    WHEN p.packageWeight IS NOT NULL THEN $PACKAGE
                    ELSE $GRAM
                END
            ) AS measurement
        FROM ProductEntity p
        LEFT JOIN (
            SELECT * 
            FROM ProductMeasurementEntity 
            WHERE productId = :productId
            ORDER BY createdAt DESC
            LIMIT 1
        ) m ON m.productId = p.id
        WHERE p.id = :productId
        LIMIT 1
        """
    )
    abstract suspend fun getProductMeasurementSuggestion(productId: Long): SuggestionVirtualEntity

    @Query(
        """
        WITH LatestMeasurements AS (
            SELECT DISTINCT m1.quantity, m1.measurement
            FROM ProductMeasurementEntity m1
            JOIN (
                SELECT m2.measurement, MAX(m2.createdAt) AS maxCreatedAt
                FROM ProductMeasurementEntity m2
                WHERE m2.productId = :productId
                GROUP BY m2.measurement
                LIMIT 3
            ) latest ON m1.measurement = latest.measurement AND m1.createdAt = latest.maxCreatedAt
            WHERE m1.productId = :productId
            GROUP BY m1.measurement
        ),
        Defaults AS (
            SELECT
                p.id AS productId,
                $SERVING AS measurement,
                1 AS quantity
            FROM ProductEntity p
            WHERE p.id = :productId AND p.servingWeight IS NOT NULL
            UNION ALL
            SELECT
                p.id AS productId,
                $PACKAGE AS measurement,
                1 AS quantity
            FROM ProductEntity p
            WHERE p.id = :productId AND p.packageWeight IS NOT NULL
            UNION ALL
            SELECT
                p.id AS productId,
                $GRAM AS measurement,
                100 AS quantity
            FROM ProductEntity p
            WHERE p.id = :productId
        )
        SELECT DISTINCT
            m.quantity, 
            m.measurement
        FROM LatestMeasurements m
        UNION ALL
        SELECT
            d.quantity, 
            d.measurement
        FROM Defaults d
        WHERE NOT EXISTS (
            SELECT 1 FROM LatestMeasurements lm WHERE lm.measurement = d.measurement
        )
        ORDER BY measurement DESC
        """
    )
    abstract suspend fun getProductMeasurementSuggestions(
        productId: Long
    ): List<SuggestionVirtualEntity>
}
