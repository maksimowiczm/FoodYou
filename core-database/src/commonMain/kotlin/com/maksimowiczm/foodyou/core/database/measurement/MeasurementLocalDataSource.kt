package com.maksimowiczm.foodyou.core.database.measurement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementLocalDataSource {

    @Query("SELECT * FROM MeasurementEntity WHERE id = :measurementId AND isDeleted = 0")
    fun observeMeasurementById(measurementId: Long): Flow<MeasurementEntity?>

    @Query(
        """
        SELECT * 
        FROM MeasurementEntity 
        WHERE 
            mealId = :mealId 
            AND epochDay = :epochDay 
            AND isDeleted = 0
        """
    )
    fun observeMeasurements(mealId: Long, epochDay: Long): Flow<List<MeasurementEntity>>

    @Insert
    suspend fun addMeasurement(measurement: MeasurementEntity): Long

    @Update
    suspend fun updateMeasurement(measurement: MeasurementEntity)

    @Transaction
    suspend fun deleteMeasurement(measurement: MeasurementEntity) {
        val updatedMeasurement = measurement.copy(isDeleted = true)
        updateMeasurement(updatedMeasurement)
    }

    @Query(
        """
        SELECT *
        FROM MeasurementEntity
        WHERE
            productId = :productId 
            AND mealId = :mealId 
            AND epochDay = :epochDay 
            AND isDeleted = 0
        """
    )
    fun observeMeasurementsByProductMealDay(
        productId: Long,
        mealId: Long,
        epochDay: Long
    ): Flow<List<MeasurementEntity>>

    @Query(
        """
        SELECT *
        FROM MeasurementEntity
        WHERE 
            recipeId = :recipeId 
            AND mealId = :mealId 
            AND epochDay = :epochDay 
            AND isDeleted = 0
        """
    )
    fun observeMeasurementsByRecipeMealDay(
        recipeId: Long,
        mealId: Long,
        epochDay: Long
    ): Flow<List<MeasurementEntity>>

    @Query(
        """
        SELECT measurement, quantity
        FROM MeasurementEntity
        WHERE
            (:productId IS NULL OR productId = :productId)
            AND (:recipeId IS NULL OR recipeId = :recipeId)
            AND (:measurement IS NULL OR measurement = :measurement)
        ORDER BY createdAt DESC
        LIMIT :limit
        """
    )
    fun observeAllMeasurementsByType(
        productId: Long? = null,
        recipeId: Long? = null,
        measurement: Measurement? = null,
        limit: Int = 1
    ): Flow<List<MeasurementSuggestion>>
}
