package com.maksimowiczm.foodyou.feature.diary.database.measurement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.maksimowiczm.foodyou.feature.diary.database.converter.WeightMeasurementSqlConstants.PACKAGE
import com.maksimowiczm.foodyou.feature.diary.database.converter.WeightMeasurementSqlConstants.SERVING
import com.maksimowiczm.foodyou.feature.diary.database.converter.WeightMeasurementSqlConstants.WEIGHT_UNIT
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
            AND wm.isDeleted = :isDeleted
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
            AND rm.isDeleted = :isDeleted
        )
        SELECT * FROM ProductMeasurement
        UNION SELECT * FROM RecipeMeasurement
        """
    )
    fun observeMeasurements(
        mealId: Long?,
        epochDay: Int,
        isDeleted: Boolean
    ): Flow<List<CombinedMeasurement>>

    @Query(
        """
        SELECT *
        FROM WeightMeasurementEntity
        WHERE id = :id
        AND isDeleted = :isDeleted
        """
    )
    fun observeProductMeasurement(id: Long, isDeleted: Boolean): Flow<WeightMeasurementEntity?>

    @Query(
        """
        SELECT *
        FROM RecipeMeasurementEntity
        WHERE id = :id
        AND isDeleted = :isDeleted
        """
    )
    fun observeRecipeMeasurement(id: Long, isDeleted: Boolean): Flow<RecipeMeasurementEntity?>

    /*
    Window function are not supported on android < 30
    @Query(
        """
        WITH Latest AS (
            SELECT
                wm.measurement AS measurement,
                wm.quantity AS quantity,
                ROW_NUMBER() OVER (PARTITION BY wm.measurement ORDER BY wm.createdAt DESC) as rn
            FROM WeightMeasurementEntity wm
            WHERE productId = :id
        ),
        DefaultValues AS (
            SELECT
                $PACKAGE AS measurement,
                1 AS quantity
            FROM ProductEntity p
            WHERE p.id = :id AND p.packageWeight IS NOT NULL
            UNION ALL
            SELECT
                $SERVING AS measurement,
                1 AS quantity
            FROM ProductEntity p
            WHERE p.id = :id AND p.servingWeight IS NOT NULL
            UNION ALL
            SELECT
                $WEIGHT_UNIT AS measurement,
                100.0 AS quantity
        ),
        CombinedValues AS (
            SELECT
                measurement,
                quantity,
                'latest' AS source
            FROM Latest
            WHERE rn = 1
            UNION ALL
            SELECT
                d.measurement,
                d.quantity,
                'default' AS source
            FROM DefaultValues d
            WHERE NOT EXISTS (
                SELECT 1
                FROM Latest l
                WHERE l.measurement = d.measurement AND l.rn = 1
            )
        )
        SELECT
            measurement,
            quantity,
            source
        FROM CombinedValues
        ORDER BY
            CASE source
                WHEN 'latest' THEN 1
                WHEN 'default' THEN 2
                ELSE 3
            END
        """
    )
     */
    @Query(
        """
        WITH Latest AS (
            SELECT wm.measurement, wm.quantity
            FROM WeightMeasurementEntity wm
            WHERE wm.productId = :id
            AND wm.createdAt = (
                SELECT MAX(wm2.createdAt)
                FROM WeightMeasurementEntity wm2
                WHERE wm2.measurement = wm.measurement
                AND wm2.productId = :id
            )
        ),
        DefaultValues AS (
            SELECT $PACKAGE AS measurement, 1 AS quantity
            FROM ProductEntity p
            WHERE p.id = :id AND p.packageWeight IS NOT NULL
            UNION ALL
            SELECT $SERVING AS measurement, 1 AS quantity
            FROM ProductEntity p
            WHERE p.id = :id AND p.servingWeight IS NOT NULL
            UNION ALL
            SELECT $WEIGHT_UNIT AS measurement, 100.0 AS quantity
        ),
        CombinedValues AS (
            SELECT l.measurement, l.quantity, 'latest' AS source
            FROM Latest l
            UNION ALL
            SELECT d.measurement, d.quantity, 'default' AS source
            FROM DefaultValues d
            WHERE NOT EXISTS (
                SELECT 1 
                FROM Latest l 
                WHERE l.measurement = d.measurement
            )
        )
        SELECT measurement, quantity, source
        FROM CombinedValues
        ORDER BY 
            CASE source 
                WHEN 'latest' THEN 1 
                WHEN 'default' THEN 2 
                ELSE 3 
            END
        """
    )
    fun observeProductMeasurementsByProductId(id: Long): Flow<List<MeasurementSuggestion>>
}
