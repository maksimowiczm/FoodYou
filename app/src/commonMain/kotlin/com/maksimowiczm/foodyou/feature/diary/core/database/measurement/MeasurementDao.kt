package com.maksimowiczm.foodyou.feature.diary.core.database.measurement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MeasurementDao {
    @Insert
    abstract suspend fun addProductMeasurement(entity: ProductMeasurementEntity)

    @Query(
        """
        SELECT *
        FROM ProductMeasurementEntity
        WHERE id = :id
        AND isDeleted = 0
        """
    )
    abstract suspend fun getProductMeasurement(id: Long): ProductMeasurementEntity?

    @Query(
        """
        UPDATE ProductMeasurementEntity
        SET isDeleted = 1
        WHERE id = :id
        """
    )
    abstract suspend fun deleteProductMeasurement(id: Long)

    @Query(
        """
        SELECT
            p.id AS productId,
            p.name AS name,
            p.brand AS brand,
            p.packageWeight AS packageWeight,
            p.servingWeight AS servingWeight,
            p.calories AS calories,
            p.proteins AS proteins,
            p.carbohydrates AS carbohydrates,
            p.sugars AS sugars,
            p.fats AS fats,
            p.saturatedFats AS saturatedFats,
            p.salt AS salt,
            p.sodium AS sodium,
            p.fiber AS fiber,
            m.id AS measurementId,
            m.measurement AS measurement,
            m.quantity AS quantity
        FROM ProductMeasurementEntity m
        LEFT JOIN ProductEntity p ON p.id = m.productId
        WHERE diaryEpochDay = :epochDay
        AND mealId = :mealId
        AND isDeleted = 0
        """
    )
    abstract fun observeMeasurements(
        epochDay: Int,
        mealId: Long
    ): Flow<List<FoodMeasurementVirtualEntity>>

    @Query(
        """
        SELECT 
            m.quantity,
            m.measurement
        FROM ProductMeasurementEntity m
        LEFT JOIN ProductEntity p ON p.id = m.productId
        WHERE p.id = :productId
        ORDER BY m.createdAt DESC
        LIMIT 1
        """
    )
    abstract suspend fun getProductMeasurementSuggestion(productId: Long): SuggestionVirtualEntity?
}
