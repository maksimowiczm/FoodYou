package com.maksimowiczm.foodyou.core.data.database.food

import androidx.room.DatabaseView
import com.maksimowiczm.foodyou.core.data.model.measurement.Measurement

@DatabaseView(
    """
    WITH Product AS (
        SELECT
            p.id AS productId,
            NULL AS recipeId,
            pm.diaryEpochDay AS epochDay,
            pm.mealId AS mealId,
            p.name AS name,
            p.brand AS brand,
            p.barcode AS barcode,
            p.calories AS calories,
            p.proteins AS proteins,
            p.carbohydrates AS carbohydrates,
            p.fats AS fats,
            p.packageWeight AS packageWeight,
            p.servingWeight AS servingWeight,
            pm.id AS measurementId,
            pm.measurement AS measurement,
            pm.quantity AS quantity
        FROM ProductEntity p
        JOIN ProductMeasurementEntity pm ON p.id = pm.productId
        WHERE pm.isDeleted = 0
    ),
    Recipe AS (
        SELECT
            NULL AS productId,
            r.id AS recipeId,
            rm.epochDay AS epochDay,
            rm.mealId AS mealId,
            r.name AS name,
            NULL AS brand,
            NULL AS barcode,
            rn.calories AS calories,
            rn.proteins AS proteins,
            rn.carbohydrates AS carbohydrates,
            rn.fats AS fats,
            rw.totalWeight AS packageWeight,
            rw.servingWeight AS servingWeight,
            rm.id AS measurementId,
            rm.measurement AS measurement,
            rm.quantity AS quantity
        FROM RecipeEntity r
        JOIN RecipeMeasurementEntity rm ON r.id = rm.recipeId
        JOIN RecipeNutritionView rn ON r.id = rn.recipeId
        JOIN RecipeWeightView rw ON r.id = rw.recipeId
        WHERE rm.isDeleted = 0
    )
    SELECT * FROM Product
    UNION ALL
    SELECT * FROM Recipe
    """
)
data class MeasuredFoodView(
    // Identity
    val productId: Long?,
    val recipeId: Long?,
    val epochDay: Int,
    val mealId: Long,

    // Food data
    val name: String,
    val brand: String?,
    val barcode: String?,
    // Nutrition per 100g
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val fats: Float,
    // Weight
    val packageWeight: Float?,
    val servingWeight: Float?,

    // Measurement
    val measurementId: Long,
    val measurement: Measurement,
    val quantity: Float
)
