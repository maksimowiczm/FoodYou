package com.maksimowiczm.foodyou.core.data.database.measurement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.maksimowiczm.foodyou.core.data.database.measurement.MeasurementSQLConstants.GRAM
import com.maksimowiczm.foodyou.core.data.database.measurement.MeasurementSQLConstants.PACKAGE
import com.maksimowiczm.foodyou.core.data.database.measurement.MeasurementSQLConstants.SERVING
import com.maksimowiczm.foodyou.core.data.model.measurement.MeasurementSuggestion
import com.maksimowiczm.foodyou.core.data.model.measurement.RecipeMeasurementEntity
import com.maksimowiczm.foodyou.core.data.model.measurement.RecipeWithMeasurement
import com.maksimowiczm.foodyou.core.domain.source.RecipeMeasurementLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecipeMeasurementDao : RecipeMeasurementLocalDataSource {
    @Insert
    abstract override suspend fun addRecipeMeasurement(entity: RecipeMeasurementEntity)

    @Update
    abstract override suspend fun updateRecipeMeasurement(entity: RecipeMeasurementEntity)

    @Query(
        """
        SELECT *
        FROM RecipeMeasurementEntity
        WHERE id = :id
        AND isDeleted = 0
        """
    )
    abstract override suspend fun getRecipeMeasurement(id: Long): RecipeMeasurementEntity?

    @Query(
        """
        UPDATE RecipeMeasurementEntity
        SET isDeleted = 1
        WHERE id = :id
        """
    )
    abstract override suspend fun deleteRecipeMeasurement(id: Long)

    @Query(
        """
        UPDATE RecipeMeasurementEntity
        SET isDeleted = 0
        WHERE id = :id
        """
    )
    abstract override suspend fun restoreRecipeMeasurement(id: Long)

    @Transaction
    @Query(
        """
        SELECT DISTINCT
            r.id AS r_id,
            r.name AS r_name,
            r.servings AS r_servings,
            m.id AS m_id,
            m.mealId AS m_mealId,
            m.epochDay AS m_epochDay,
            m.recipeId AS m_recipeId,
            m.measurement AS m_measurement,
            m.quantity AS m_quantity,
            m.createdAt AS m_createdAt,
            m.isDeleted AS m_isDeleted
        FROM RecipeEntity r
        LEFT JOIN RecipeMeasurementEntity m ON r.id = m.recipeId
        LEFT JOIN RecipeIngredientProductDetails i ON i.r_recipeId = r.id
        WHERE m.epochDay = :epochDay
        AND m.mealId = :mealId
        AND m.isDeleted = 0
        """
    )
    abstract override fun observeRecipeMeasurements(
        epochDay: Int,
        mealId: Long
    ): Flow<List<RecipeWithMeasurement>>

    @Transaction
    @Query(
        """
        SELECT DISTINCT
            r.id AS r_id,
            r.name AS r_name,
            r.servings AS r_servings,
            m.id AS m_id,
            m.mealId AS m_mealId,
            m.epochDay AS m_epochDay,
            m.recipeId AS m_recipeId,
            m.measurement AS m_measurement,
            m.quantity AS m_quantity,
            m.createdAt AS m_createdAt,
            m.isDeleted AS m_isDeleted
        FROM RecipeEntity r
        LEFT JOIN RecipeMeasurementEntity m ON r.id = m.recipeId
        LEFT JOIN RecipeIngredientProductDetails i ON i.r_recipeId = r.id
        WHERE m.id = :measurementId
        AND m.isDeleted = 0
        """
    )
    abstract override fun observeRecipeMeasurement(
        measurementId: Long
    ): Flow<RecipeWithMeasurement?>

    @Query(
        """
        WITH LatestMeasurements AS (
            SELECT DISTINCT m1.quantity, m1.measurement
            FROM RecipeMeasurementEntity m1
            JOIN (
                SELECT m2.measurement, MAX(m2.createdAt) AS maxCreatedAt
                FROM RecipeMeasurementEntity m2
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
        ORDER BY measurement DESC
        """
    )
    abstract override suspend fun getRecipeMeasurementSuggestions(
        recipeId: Long
    ): List<MeasurementSuggestion>
}
