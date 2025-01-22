package com.maksimowiczm.foodyou.feature.diary.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query(
        """
        SELECT $ProductWithWeightMeasurementSqlFields
        FROM WeightMeasurementEntity wm
        INNER JOIN ProductEntity p ON p.id = wm.productId
        WHERE isDeleted = 0
        AND diaryEpochDay = :epochDay 
        AND (:mealId IS NULL OR mealId = :mealId)
        ORDER BY wm.createdAt DESC
        """
    )
    fun productsWithMeasurementStream(
        epochDay: Long,
        mealId: Long? = null
    ): Flow<List<ProductWithWeightMeasurement>>
}
