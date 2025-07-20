package com.maksimowiczm.foodyou.feature.fooddiary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface MeasurementDao {

    @Insert
    suspend fun insertMeasurement(measurement: Measurement)

    @Insert
    suspend fun insertMeasurements(measurements: List<Measurement>)

    @Transaction
    suspend fun replaceMeasurement(measurementId: Long, measurements: List<Measurement>) {
        val measurement = observeMeasurementById(measurementId).first()

        if (measurement == null) {
            return
        }

        deleteMeasurement(measurement)
        insertMeasurements(measurements)
    }

    @Update
    suspend fun updateMeasurement(measurement: Measurement)

    @Transaction
    suspend fun deleteMeasurement(measurement: Measurement) {
        val updatedMeasurement = measurement.copy(isDeleted = true)
        updateMeasurement(updatedMeasurement)
    }

    @Query(
        """
        SELECT *
        FROM Measurement
        WHERE 
            id = :measurementId 
            AND isDeleted = 0
        """
    )
    fun observeMeasurementById(measurementId: Long): Flow<Measurement?>

    @Query(
        """
        SELECT * 
        FROM Measurement
        WHERE 
            mealId = :mealId 
            AND epochDay = :epochDay 
            AND isDeleted = 0
        """
    )
    fun observeMeasurements(mealId: Long, epochDay: Long): Flow<List<Measurement>>

    @Query(
        """
        SELECT DISTINCT measurement, quantity
        FROM Measurement
        WHERE CASE 
            WHEN :productId IS NOT NULL THEN productId = :productId
            WHEN :recipeId IS NOT NULL THEN recipeId = :recipeId
            ELSE 0 
        END
        ORDER BY createdAt DESC
        LIMIT :limit
        """
    )
    fun observeMeasurementSuggestions(
        productId: Long?,
        recipeId: Long?,
        limit: Int
    ): Flow<List<MeasurementSuggestion>>

    @Transaction
    @Query(
        """
        SELECT m.*
        FROM Measurement m
        LEFT JOIN Product p ON m.productId == p.id
        LEFT JOIN Recipe r ON m.recipeId == r.id
        WHERE 
            m.mealId = :mealId 
            AND m.epochDay = :epochDay 
            AND m.isDeleted = 0
        """
    )
    fun observeFoodWithMeasurement(mealId: Long, epochDay: Long): Flow<List<FoodWithMeasurement>>
}
