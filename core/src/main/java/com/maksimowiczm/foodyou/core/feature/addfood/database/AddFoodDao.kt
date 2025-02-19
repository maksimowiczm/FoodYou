package com.maksimowiczm.foodyou.core.feature.addfood.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AddFoodDao {

    @Query(
        """
        SELECT *
        FROM WeightMeasurementEntity
        WHERE isDeleted = 0
        AND id = :portionId
        """
    )
    fun observeWeightMeasurement(portionId: Long): Flow<WeightMeasurementEntity?>

    @Insert
    suspend fun insertWeightMeasurement(weightMeasurement: WeightMeasurementEntity): Long

    @Query(
        """
        UPDATE WeightMeasurementEntity
        SET isDeleted = 1
        WHERE id = :id
        """
    )
    suspend fun deleteWeightMeasurement(id: Long)

    @Upsert
    suspend fun upsertProductQuery(productQueryEntity: ProductQueryEntity)

    @Query(
        """
        SELECT *
        FROM ProductQueryEntity
        ORDER BY date DESC
        LIMIT :limit
        """
    )
    fun observeLatestQueries(limit: Int): Flow<List<ProductQueryEntity>>

    @Transaction
    @Query(
        """
        SELECT wm.*
        FROM WeightMeasurementEntity wm
        INNER JOIN ProductEntity p ON wm.productId = p.id
        WHERE wm.diaryEpochDay = :epochDay
        AND (:mealId IS NULL OR wm.mealId = :mealId)
        AND wm.isDeleted = 0
        """
    )
    fun observeMeasuredProducts(
        mealId: Long?,
        epochDay: Int
    ): Flow<List<ProductWithWeightMeasurementEntity>>

    @Query(
        """
        SELECT measurement, quantity
        FROM WeightMeasurementEntity
        WHERE productId = :productId
        GROUP BY measurement
        ORDER BY MAX(createdAt) DESC
        """
    )
    fun observeQuantitySuggestionsByProductId(productId: Long): Flow<List<QuantitySuggestionEntity>>

    @Query(
        """
        SELECT *
        FROM MealEntity
        """
    )
    fun observeMeals(): Flow<List<MealEntity>>

    @Insert
    suspend fun insertMeal(meal: MealEntity)

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query(
        """
        SELECT 
            p.id AS productId, 
            CASE 
                WHEN wm.isDeleted == 0 
                AND (:mealId IS NULL OR wm.mealId = :mealId) 
                AND wm.diaryEpochDay = :epochDay 
                THEN wm.id
                ELSE NULL
            END AS measurementId,
            CASE 
                WHEN wm.rank IS NULL THEN ${WeightMeasurementEntity.FIRST_RANK}
                ELSE wm.rank
            END AS realRank
        FROM ProductEntity p
        LEFT JOIN WeightMeasurementEntity wm ON p.id = wm.productId
        WHERE (:query IS NULL OR p.name LIKE '%' || :query || '%' OR p.brand LIKE '%' || :query || '%')
        AND (:barcode IS NULL OR p.barcode = :barcode) 
        ORDER BY productId, realRank
        """
    )
    fun observeProductIdsWithMeasurementIds(
        mealId: Long?,
        epochDay: Int,
        query: String?,
        barcode: String?
    ): Flow<List<ProductWeightMeasurementJunction>>

    @Transaction
    @Query(
        """
        SELECT wm.*
        FROM WeightMeasurementEntity wm
        LEFT JOIN ProductEntity p ON p.id = wm.productId
        WHERE wm.isDeleted IS NULL OR wm.isDeleted = 0
        AND wm.id = :measurementId
        """
    )
    fun observeMeasurement(measurementId: Long): Flow<ProductWithWeightMeasurementEntity?>

    @Transaction
    @Query(
        """
        SELECT wm.*
        FROM WeightMeasurementEntity wm
        WHERE wm.productId = :productId
        ORDER BY wm.createdAt DESC
        LIMIT 1
        """
    )
    fun observeLatestMeasurementByProductId(productId: Long): Flow<ProductWithWeightMeasurementEntity?>

    @Query(
        """
        SELECT *
        FROM WeightMeasurementEntity
        WHERE productId = :productId
        AND mealId = :mealId
        AND diaryEpochDay = :epochDay
        AND (:isDeleted IS NULL OR isDeleted = :isDeleted)
        """
    )
    suspend fun getWeightMeasurements(
        productId: Long,
        mealId: Long?,
        epochDay: Int,
        isDeleted: Boolean?
    ): List<WeightMeasurementEntity>
}
