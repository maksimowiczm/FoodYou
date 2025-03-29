package com.maksimowiczm.foodyou.feature.diary.database.measurement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM WeightMeasurementEntity WHERE isDeleted = :isDeleted")
    suspend fun getProductWeightMeasurementById(
        id: Long,
        isDeleted: Boolean
    ): WeightMeasurementEntity?

    @Query("SELECT * FROM RecipeMeasurementEntity WHERE isDeleted = :isDeleted")
    suspend fun getRecipeWeightMeasurementById(
        id: Long,
        isDeleted: Boolean
    ): RecipeMeasurementEntity?

    @Insert
    suspend fun insertWeightMeasurement(entity: WeightMeasurementEntity)

    @Insert
    suspend fun insertWeightMeasurement(entity: RecipeMeasurementEntity)

    @Update
    suspend fun updateWeightMeasurement(entity: WeightMeasurementEntity)

    @Update
    suspend fun updateWeightMeasurement(entity: RecipeMeasurementEntity)

    @Query(
        """
        UPDATE WeightMeasurementEntity
        SET isDeleted = 1
        WHERE id = :id
        """
    )
    suspend fun removeProductWeightMeasurement(id: Long)

    @Query(
        """
        UPDATE RecipeMeasurementEntity
        SET isDeleted = 1
        WHERE id = :id
        """
    )
    suspend fun removeRecipeWeightMeasurement(id: Long)

    @Query(
        """
        UPDATE WeightMeasurementEntity
        SET isDeleted = 0
        WHERE id = :id
        """
    )
    suspend fun restoreProductWeightMeasurement(id: Long)

    @Query(
        """
        UPDATE RecipeMeasurementEntity
        SET isDeleted = 0
        WHERE id = :id
        """
    )
    suspend fun restoreRecipeWeightMeasurement(id: Long)
}
