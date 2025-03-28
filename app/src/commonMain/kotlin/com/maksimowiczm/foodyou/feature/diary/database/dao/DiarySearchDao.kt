package com.maksimowiczm.foodyou.feature.diary.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.maksimowiczm.foodyou.feature.diary.database.entity.DiarySearchEntity

@Dao
interface DiarySearchDao {
    @Query(
        """
        WITH
        ProductsTodayMeasurements AS (
            SELECT 
                *,
                1 AS todaysMeasurement
            FROM WeightMeasurementEntity wm
            WHERE (wm.mealId = :mealId AND wm.diaryEpochDay = :epochDay AND isDeleted = 0)
        ),
        ProductsNotTodayMeasurements AS (
           SELECT
                *,
                0 AS todaysMeasurement
            FROM WeightMeasurementEntity wm
            WHERE wm.productId NOT IN (
                SELECT productId 
                FROM ProductsTodayMeasurements
            )
            AND wm.createdAt = (
                SELECT MAX(wm2.createdAt) 
                FROM WeightMeasurementEntity wm2 
                WHERE wm2.productId = wm.productId
            )
            GROUP BY wm.productId
        ),
        ProductsSuggestions AS (
            SELECT * FROM ProductsTodayMeasurements 
            UNION SELECT * FROM ProductsNotTodayMeasurements
        ),
        Products AS (
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
                s.id AS pm_id,
                s.mealId AS pm_mealId,
                s.diaryEpochDay AS pm_diaryEpochDay,
                s.productId AS pm_productId,
                s.createdAt AS pm_createdAt,
                s.measurement AS pm_measurement,
                s.quantity AS pm_quantity,
                s.isDeleted AS pm_isDeleted,
                s.todaysMeasurement,
                NULL AS r_id,
                NULL AS r_name,
                NULL AS r_servings,
                NULL AS rm_id,
                NULL AS rm_mealId,
                NULL AS rm_diaryEpochDay,
                NULL AS rm_recipeId,
                NULL AS rm_createdAt,
                NULL AS rm_measurement,
                NULL AS rm_quantity,
                NULL AS rm_isDeleted
            FROM ProductEntity p
            LEFT JOIN ProductsSuggestions s ON s.productId = p.id
            WHERE (:query IS NULL OR p.name LIKE '%' || :query || '%' OR p.brand LIKE '%' || :query || '%')
            ORDER BY p.id, s.id
        ),
        RecipeTodayMeasurements AS (
            SELECT 
                *,
                1 AS todaysMeasurement
            FROM RecipeMeasurementEntity rm
            WHERE (rm.mealId = :mealId AND rm.diaryEpochDay = :epochDay AND isDeleted = 0)
        ),
        RecipeNotTodayMeasurements AS (
           SELECT
                *,
                0 AS todaysMeasurement
            FROM RecipeMeasurementEntity rm
            WHERE rm.recipeId NOT IN (
                SELECT recipeId 
                FROM RecipeTodayMeasurements
            )
            AND rm.createdAt = (
                SELECT MAX(rm2.createdAt) 
                FROM RecipeMeasurementEntity rm2 
                WHERE rm2.recipeId = rm.recipeId
            )
            GROUP BY rm.recipeId
        ),
        RecipeSuggestions AS (
            SELECT * FROM RecipeTodayMeasurements 
            UNION SELECT * FROM RecipeNotTodayMeasurements
        ),
        Recipes AS (
            SELECT 
                NULL AS p_id,
                NULL AS p_name,
                NULL AS p_brand,
                NULL AS p_barcode,
                NULL AS p_calories,
                NULL AS p_proteins,
                NULL AS p_carbohydrates,
                NULL AS p_sugars,
                NULL AS p_fats,
                NULL AS p_saturatedFats,
                NULL AS p_salt,
                NULL AS p_sodium,
                NULL AS p_fiber,
                NULL AS p_packageWeight,
                NULL AS p_servingWeight,
                NULL AS p_weightUnit,
                NULL AS p_productSource,
                NULL AS pm_id,
                NULL AS pm_mealId,
                NULL AS pm_diaryEpochDay,
                NULL AS pm_productId,
                NULL AS pm_createdAt,
                NULL AS pm_measurement,
                NULL AS pm_quantity,
                NULL AS pm_isDeleted,
                r.id AS r_id,
                r.name AS r_name,
                r.servings AS r_servings,
                s.id AS rm_id,
                s.mealId AS rm_mealId,
                s.diaryEpochDay AS rm_diaryEpochDay,
                s.recipeId AS rm_recipeId,
                s.createdAt AS rm_createdAt,
                s.measurement AS rm_measurement,
                s.quantity AS rm_quantity,
                s.isDeleted AS rm_isDeleted,
                s.todaysMeasurement
            FROM RecipeEntity r
            LEFT JOIN RecipeSuggestions s ON s.recipeId = r.id
            WHERE (:query IS NULL OR r.name LIKE '%' || :query || '%')
            ORDER BY r.id, s.id
        )
        SELECT * FROM Products
        UNION SELECT * FROM Recipes
        WHERE :query IS NULL 
        OR r_name LIKE '%' || :query || '%' 
        OR p_name LIKE '%' || :query || '%'
        OR p_brand LIKE '%' || :query || '%'
        """
    )
    fun queryDiary(
        query: String?,
        mealId: Long,
        epochDay: Int
    ): PagingSource<Int, DiarySearchEntity>
}
