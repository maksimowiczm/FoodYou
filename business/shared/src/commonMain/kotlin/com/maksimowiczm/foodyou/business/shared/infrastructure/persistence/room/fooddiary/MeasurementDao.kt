package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
abstract class MeasurementDao {

    @Insert abstract suspend fun insertMeasurement(measurement: MeasurementEntity): Long

    @Insert abstract suspend fun insertMeasurements(measurements: List<MeasurementEntity>)

    @Transaction
    open suspend fun replaceMeasurement(
        measurementId: Long,
        measurements: List<MeasurementEntity>,
    ) {
        val measurement = observeMeasurementById(measurementId).first()

        if (measurement == null) {
            return
        }

        deleteMeasurement(measurement)
        insertMeasurements(measurements)
    }

    @Update abstract suspend fun updateMeasurement(measurement: MeasurementEntity)

    @Delete abstract suspend fun deleteMeasurement(measurement: MeasurementEntity)

    @Query(
        """
        SELECT *
        FROM Measurement
        WHERE id = :measurementId 
        """
    )
    abstract fun observeMeasurementById(measurementId: Long): Flow<MeasurementEntity?>

    @Query(
        """
        SELECT * 
        FROM Measurement
        WHERE 
            mealId = :mealId 
            AND epochDay = :epochDay 
        """
    )
    abstract fun observeMeasurements(mealId: Long, epochDay: Long): Flow<List<MeasurementEntity>>

    @Query(
        """
        SELECT *
        FROM DiaryProduct
        WHERE id = :id
        """
    )
    abstract fun observeDiaryProduct(id: Long): Flow<DiaryProductEntity?>

    @Insert abstract suspend fun insertDiaryProduct(diaryProduct: DiaryProductEntity): Long

    @Insert
    abstract suspend fun insertDiaryRecipeIngredient(
        diaryRecipeIngredient: DiaryRecipeIngredientEntity
    )

    @Insert abstract suspend fun insertDiaryRecipe(diaryRecipe: DiaryRecipeEntity): Long

    @Query(
        """
        SELECT *
        FROM DiaryRecipe
        WHERE id = :id
        """
    )
    abstract fun observeDiaryRecipe(id: Long): Flow<DiaryRecipeEntity?>

    @Query(
        """
        SELECT *
        FROM DiaryRecipeIngredient
        WHERE recipeId = :id
        """
    )
    abstract fun observeDiaryRecipeIngredients(id: Long): Flow<List<DiaryRecipeIngredientEntity>>

    @Delete abstract suspend fun deleteDiaryProduct(diaryProduct: DiaryProductEntity)

    @Delete abstract suspend fun deleteDiaryRecipe(diaryRecipe: DiaryRecipeEntity)
}
