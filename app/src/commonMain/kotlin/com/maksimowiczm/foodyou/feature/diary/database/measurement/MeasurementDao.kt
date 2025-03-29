package com.maksimowiczm.foodyou.feature.diary.database.measurement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    @Query(
        """
        SELECT *
        FROM WeightMeasurementEntity
        WHERE isDeleted = :isDeleted
        AND id = :id
        """
    )
    suspend fun getProductWeightMeasurementById(
        id: Long,
        isDeleted: Boolean
    ): WeightMeasurementEntity?

    @Query(
        """
        SELECT *
        FROM RecipeMeasurementEntity
        WHERE isDeleted = :isDeleted
        AND id = :id
        """
    )
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

    @Query(
        """
        WITH 
        ProductMeasurement AS (
            SELECT
                wm.productId AS productId,
                NULL AS recipeId,
                wm.mealId AS mealId,
                wm.diaryEpochDay AS diaryEpochDay,
                wm.id AS measurementId,
                wm.measurement AS measurement,
                wm.quantity AS quantity,
                wm.createdAt AS createdAt,
                wm.isDeleted AS isDeleted
            FROM WeightMeasurementEntity wm
            WHERE diaryEpochDay = :epochDay
            AND (:mealId IS NULL OR wm.mealId = :mealId)
            AND wm.isDeleted = 0
        ),
        RecipeMeasurement AS (
            SELECT
                NULL AS productId,
                rm.recipeId AS recipeId,
                rm.mealId AS mealId,
                rm.diaryEpochDay AS diaryEpochDay,
                rm.id AS measurementId,
                rm.measurement AS measurement,
                rm.quantity AS quantity,
                rm.createdAt AS createdAt,
                rm.isDeleted AS isDeleted
            FROM RecipeMeasurementEntity rm
            WHERE diaryEpochDay = :epochDay
            AND (:mealId IS NULL OR rm.mealId = :mealId)
            AND rm.isDeleted = 0
        )
        SELECT * FROM ProductMeasurement
        UNION SELECT * FROM RecipeMeasurement
        """
    )
    fun observeMeasurements(mealId: Long?, epochDay: Int): Flow<List<CombinedMeasurement>>
}
