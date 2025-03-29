package com.maksimowiczm.foodyou.feature.diary.database.search

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query

@Dao
interface SearchDao {
    /**
     * Combine product and recipe search results into a single list of DiarySearchEntity. Where
     * food that was added today (mealId and epochDay) is flagged with todaysMeasurement = 1.
     */
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
                p.id AS productId,
                NULL AS recipeId,
                p.name AS name,
                p.brand AS brand,
                p.calories AS calories,
                p.proteins AS proteins,
                p.carbohydrates AS carbohydrates,
                p.fats AS fats,
                p.packageWeight AS packageWeight,
                p.servingWeight AS servingWeight,
                NULL AS servings,
                CASE WHEN s.todaysMeasurement = 1 
                    THEN s.id
                    ELSE NULL 
                END AS measurementId,
                s.measurement AS measurement,
                s.quantity AS quantity
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
        RecipieSuggestionsWithNutrition AS (
            SELECT * 
            FROM RecipeSuggestions
            LEFT JOIN RecipeNutritionView r 
            ON r.recipeId = RecipeSuggestions.recipeId
        ),
        Recipes AS (
            SELECT 
                NULL AS productId,
                r.id AS recipeId,
                r.name AS name,
                NULL AS brand,
                s.totalCalories AS calories,
                s.totalProteins AS proteins,
                s.totalCarbohydrates AS carbohydrates,
                s.totalFats AS fats,
                s.totalWeight AS packageWeight,
                NULL AS servingWeight,
                r.servings AS servings,
                s.id AS measurementId,
                s.measurement AS measurement,
                s.quantity AS quantity
            FROM RecipeEntity r
            LEFT JOIN RecipieSuggestionsWithNutrition s ON s.recipeId = r.id
            WHERE (:query IS NULL OR r.name LIKE '%' || :query || '%')
            ORDER BY r.id, s.id
        )
        SELECT * FROM Products
        UNION SELECT * FROM Recipes
        """
    )
    fun queryDiary(
        query: String?,
        mealId: Long,
        epochDay: Int
    ): PagingSource<Int, DiarySearchEntity>
}
