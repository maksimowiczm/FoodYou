package com.maksimowiczm.foodyou.core.data.database.measurement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.maksimowiczm.foodyou.core.data.model.measurement.MeasurementSuggestion
import com.maksimowiczm.foodyou.core.data.model.measurement.ProductMeasurementEntity
import com.maksimowiczm.foodyou.core.data.model.measurement.ProductWithMeasurement
import com.maksimowiczm.foodyou.core.domain.source.ProductMeasurementLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductMeasurementDao : ProductMeasurementLocalDataSource {
    @Insert
    abstract override suspend fun addProductMeasurement(entity: ProductMeasurementEntity)

    @Update
    abstract override suspend fun updateProductMeasurement(entity: ProductMeasurementEntity)

    @Query(
        """
        SELECT *
        FROM ProductMeasurementEntity
        WHERE id = :id
        AND isDeleted = 0
        """
    )
    abstract override suspend fun getProductMeasurement(id: Long): ProductMeasurementEntity?

    @Query(
        """
        UPDATE ProductMeasurementEntity
        SET isDeleted = 1
        WHERE id = :id
        """
    )
    abstract override suspend fun deleteProductMeasurement(id: Long)

    @Query(
        """
        UPDATE ProductMeasurementEntity
        SET isDeleted = 0
        WHERE id = :id
        """
    )
    abstract override suspend fun restoreProductMeasurement(id: Long)

    @Transaction
    @Query(
        """
        SELECT m.*
        FROM ProductMeasurementEntity m
        LEFT JOIN ProductEntity p ON p.id = m.productId
        WHERE diaryEpochDay = :epochDay
        AND mealId = :mealId
        AND isDeleted = 0
        ORDER BY m.createdAt DESC
        """
    )
    abstract override fun observeProductMeasurements(
        epochDay: Int,
        mealId: Long
    ): Flow<List<ProductWithMeasurement>>

    @Transaction
    @Query(
        """
        SELECT m.* 
        FROM ProductMeasurementEntity m
        LEFT JOIN ProductEntity p ON p.id = m.productId
        WHERE m.id = :measurementId
        AND m.isDeleted = 0
        """
    )
    abstract override fun observeProductMeasurement(
        measurementId: Long
    ): Flow<ProductWithMeasurement?>

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
                ${MeasurementSQLConstants.SERVING} AS measurement,
                1 AS quantity
            FROM ProductEntity p
            WHERE p.id = :productId AND p.servingWeight IS NOT NULL
            UNION ALL
            SELECT
                p.id AS productId,
                ${MeasurementSQLConstants.PACKAGE} AS measurement,
                1 AS quantity
            FROM ProductEntity p
            WHERE p.id = :productId AND p.packageWeight IS NOT NULL
            UNION ALL
            SELECT
                p.id AS productId,
                ${MeasurementSQLConstants.GRAM} AS measurement,
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
        ORDER BY measurement
        """
    )
    abstract override fun observeProductMeasurementSuggestions(
        productId: Long
    ): Flow<List<MeasurementSuggestion>>

    @Query(
        """
        SELECT *
        FROM ProductMeasurementEntity
        WHERE productId = :productId
        ORDER BY createdAt DESC
        LIMIT 1
        """
    )
    abstract override fun observeLatestProductMeasurementSuggestion(
        productId: Long
    ): Flow<MeasurementSuggestion?>
}
