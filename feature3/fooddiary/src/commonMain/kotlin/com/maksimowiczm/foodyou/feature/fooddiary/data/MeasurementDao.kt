package com.maksimowiczm.foodyou.feature.fooddiary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    @Insert
    suspend fun insertMeasurement(measurement: Measurement)

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
    fun observeMeasurementById(measurementId: Long): Flow<Measurement>

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
}
