package com.maksimowiczm.foodyou.core.database.search

import androidx.room.DatabaseView
import com.maksimowiczm.foodyou.core.database.measurement.Measurement

@DatabaseView(
    """
    WITH Product AS (
        SELECT
            p.id AS productId,
            NULL AS recipeId,
            p.name AS name,
            p.brand AS brand,
            p.barcode AS barcode,
            p.calories AS calories,
            p.proteins AS proteins,
            p.carbohydrates AS carbohydrates,
            p.fats AS fats,
            p.packageWeight AS packageWeight,
            p.servingWeight AS servingWeight,

            CASE 
                WHEN pm.measurement IS NOT NULL THEN pm.measurement
                WHEN p.servingWeight IS NOT NULL THEN 2
                WHEN p.packageWeight IS NOT NULL THEN 1
                ELSE 0
            END AS measurement,

            CASE 
                WHEN pm.quantity IS NOT NULL THEN pm.quantity
                WHEN p.servingWeight IS NOT NULL THEN 1
                WHEN p.packageWeight IS NOT NULL THEN 1
                ELSE 100
            END AS quantity

        FROM ProductEntity p
        LEFT JOIN ProductMeasurementEntity pm ON p.id = pm.productId
        WHERE 
            pm.createdAt IS NULL 
            OR pm.createdAt = (
                SELECT MAX(createdAt)
                FROM ProductMeasurementEntity
                WHERE productId = pm.productId
            )
    ),
    Recipe AS (
        SELECT
            NULL AS productId,
            r.id AS recipeId,
            r.name AS name,
            NULL AS brand,
            NULL AS barcode,
            rn.totalCalories / rw.totalWeight * 100 AS calories,
            rn.totalProteins / rw.totalWeight * 100 AS proteins,
            rn.totalCarbohydrates / rw.totalWeight * 100 AS carbohydrates,
            rn.totalFats / rw.totalWeight * 100 AS fats,
            rw.totalWeight AS packageWeight,
            rw.servingWeight AS servingWeight,
            CASE 
                WHEN rm.measurement IS NOT NULL THEN rm.measurement
                ELSE 2
            END AS measurement,

            CASE 
                WHEN rm.quantity IS NOT NULL THEN rm.quantity
                ELSE 1
            END AS quantity
            
        FROM RecipeEntity r
        LEFT JOIN RecipeMeasurementEntity rm ON r.id = rm.recipeId
        LEFT JOIN RecipeNutritionView rn ON r.id = rn.recipeId
        LEFT JOIN RecipeWeightView rw ON r.id = rw.recipeId
        WHERE
            rm.createdAt IS NULL 
            OR rm.createdAt = (
                SELECT MAX(createdAt)
                FROM RecipeMeasurementEntity
                WHERE recipeId = rm.recipeId
            )
    )
    SELECT * FROM Product p
    UNION ALL
    SELECT * FROM Recipe r
    """
)
data class MeasurementSuggestionView(
    // Identity
    val productId: Long?,
    val recipeId: Long?,

    // Food
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
    val measurement: Measurement?,
    val quantity: Float?
)
