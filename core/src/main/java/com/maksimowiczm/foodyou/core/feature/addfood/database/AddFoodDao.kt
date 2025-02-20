package com.maksimowiczm.foodyou.core.feature.addfood.database

import androidx.paging.PagingSource
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
    /**
     * Get all products and all of their measurements for the given meal and epoch day. If product
     * is not measured for the given meal and epoch day, get the latest measurement for the product.
     * If product is not measured at all, return null for the measurement.
     *
     * If measurement is in given meal and epoch day, hasMeasurement is set to 1, otherwise 0.
     */
    @Query(
        """
        WITH 
        TodayMeasurements AS (
            SELECT 
                *,
                1 AS todaysMeasurement
            FROM WeightMeasurementEntity wm
            WHERE (wm.mealId = :mealId AND wm.diaryEpochDay = :epochDay AND isDeleted = 0)
        ),
        NotTodayMeasurements AS (
           SELECT
                *,
                0 AS todaysMeasurement
            FROM WeightMeasurementEntity wm
            WHERE wm.productId NOT IN (
                SELECT productId 
                FROM TodayMeasurements
            )
            AND wm.createdAt = (
                SELECT MAX(wm2.createdAt) 
                FROM WeightMeasurementEntity wm2 
                WHERE wm2.productId = wm.productId
            )
            GROUP BY wm.productId
        ),
        Suggestions AS (
            SELECT * FROM TodayMeasurements 
            UNION SELECT * FROM NotTodayMeasurements
        )
        SELECT 
            p.id AS p_id,
            p.name AS p_name,
            p.brand AS p_brand,
            p.barcode AS p_barcode,
            p.calories AS p_calories,
            p.proteins AS p_proteins,
            p.carbohydrates AS p_carbohydrates,
            p.sugars AS p_sugars,
            p.fats AS p_fats,
            p.saturatedFats AS p_saturatedFats,
            p.salt AS p_salt,
            p.sodium AS p_sodium,
            p.fiber AS p_fiber,
            p.packageWeight AS p_packageWeight,
            p.servingWeight AS p_servingWeight,
            p.weightUnit AS p_weightUnit,
            p.productSource AS p_productSource,
            s.id AS m_id,
            s.mealId AS m_mealId,
            s.diaryEpochDay AS m_diaryEpochDay,
            s.productId AS m_productId,
            s.createdAt AS m_createdAt,
            s.measurement AS m_measurement,
            s.quantity AS m_quantity,
            s.isDeleted AS m_isDeleted,
            s.todaysMeasurement
        FROM ProductEntity p
        LEFT JOIN Suggestions s ON s.productId = p.id
        WHERE (:query IS NULL OR p.name LIKE '%' || :query || '%' OR p.brand LIKE '%' || :query || '%')
        AND (:barcode IS NULL OR p.barcode = :barcode)
        ORDER BY p.id, s.id
        """
    )
    fun observePagedProductsWithMeasurement(
        mealId: Long,
        epochDay: Int,
        query: String?,
        barcode: String?
    ): PagingSource<Int, ProductSearchEntity>

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
