package com.maksimowiczm.foodyou.core.database.measurement

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.GRAM
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.PACKAGE
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.SERVING
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementLocalDataSource {

    @Query("SELECT * FROM MeasurementEntity WHERE id = :measurementId")
    fun observeMeasurementById(measurementId: Long): Flow<MeasurementEntity?>

    @Query("SELECT * FROM MeasurementEntity WHERE mealId = :mealId AND epochDay = :epochDay")
    fun observeMeasurements(mealId: Long, epochDay: Int): Flow<List<MeasurementEntity>>

    @Insert
    suspend fun addMeasurement(measurement: MeasurementEntity): Long

    @Update
    suspend fun updateMeasurement(measurement: MeasurementEntity)

    @Delete
    suspend fun deleteMeasurement(measurement: MeasurementEntity)

    @Query(
        """
        SELECT *
        FROM MeasurementEntity
        WHERE productId = :productId
        ORDER BY createdAt DESC
        LIMIT 1
        """
    )
    fun observeLatestProductMeasurementSuggestion(productId: Long): Flow<MeasurementSuggestion?>

    @Query(
        """
        SELECT *
        FROM MeasurementEntity
        WHERE recipeId = :recipeId
        ORDER BY createdAt DESC
        LIMIT 1
        """
    )
    fun observeLatestRecipeMeasurementSuggestion(recipeId: Long): Flow<MeasurementSuggestion?>

    @Query(
        """
        WITH LatestMeasurements AS (
            SELECT DISTINCT m1.quantity, m1.measurement
            FROM MeasurementEntity m1
            JOIN (
                SELECT m2.measurement, MAX(m2.createdAt) AS maxCreatedAt
                FROM MeasurementEntity m2
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
        """
    )
    fun observeProductMeasurementSuggestions(productId: Long): Flow<List<MeasurementSuggestion>>

    @Query(
        """
        WITH LatestMeasurements AS (
            SELECT DISTINCT m1.quantity, m1.measurement
            FROM MeasurementEntity m1
            JOIN (
                SELECT m2.measurement, MAX(m2.createdAt) AS maxCreatedAt
                FROM MeasurementEntity m2
                WHERE m2.recipeId = :recipeId
                GROUP BY m2.measurement
                LIMIT 3
            ) latest ON m1.measurement = latest.measurement AND m1.createdAt = latest.maxCreatedAt
            WHERE m1.recipeId = :recipeId
            GROUP BY m1.measurement
        ),
        Defaults AS (
            SELECT
                r.id AS recipeId,
                $SERVING AS measurement,
                1 AS quantity
            FROM RecipeEntity r
            WHERE r.id = :recipeId
            UNION ALL
            SELECT
                r.id AS recipeId,
                $PACKAGE AS measurement,
                1 AS quantity
            FROM RecipeEntity r
            WHERE r.id = :recipeId
            UNION ALL
            SELECT
                r.id AS recipeId,
                $GRAM AS measurement,
                100 AS quantity
            FROM RecipeEntity r
            WHERE r.id = :recipeId
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
    fun observeRecipeMeasurementSuggestions(recipeId: Long): Flow<List<MeasurementSuggestion>>
}
