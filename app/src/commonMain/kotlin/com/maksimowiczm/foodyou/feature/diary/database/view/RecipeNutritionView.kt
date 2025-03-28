package com.maksimowiczm.foodyou.feature.diary.database.view

import com.maksimowiczm.foodyou.feature.diary.database.converter.SQL_CONSTANTS

import androidx.room.DatabaseView

@DatabaseView(
    """
    SELECT 
        r.id AS recipeId,
        SUM(
            CASE 
                WHEN ri.measurement = ${SQL_CONSTANTS.WEIGHT_UNIT} THEN p.calories * (ri.quantity / 100.0)
                WHEN ri.measurement = ${SQL_CONSTANTS.PACKAGE} AND p.packageWeight IS NOT NULL THEN p.calories * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = ${SQL_CONSTANTS.SERVING} AND p.servingWeight IS NOT NULL THEN p.calories * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) AS totalCalories,
        SUM(
            CASE 
                WHEN ri.measurement = ${SQL_CONSTANTS.WEIGHT_UNIT} THEN p.proteins * (ri.quantity / 100.0)
                WHEN ri.measurement = ${SQL_CONSTANTS.PACKAGE} AND p.packageWeight IS NOT NULL THEN p.proteins * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = ${SQL_CONSTANTS.SERVING} AND p.servingWeight IS NOT NULL THEN p.proteins * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) AS totalProteins,
        SUM(
            CASE 
                WHEN ri.measurement = ${SQL_CONSTANTS.WEIGHT_UNIT} THEN p.carbohydrates * (ri.quantity / 100.0)
                WHEN ri.measurement = ${SQL_CONSTANTS.PACKAGE} AND p.packageWeight IS NOT NULL THEN p.carbohydrates * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = ${SQL_CONSTANTS.SERVING} AND p.servingWeight IS NOT NULL THEN p.carbohydrates * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) AS totalCarbohydrates,
        SUM(
            CASE 
                WHEN ri.measurement = ${SQL_CONSTANTS.WEIGHT_UNIT} THEN p.fats * (ri.quantity / 100.0)
                WHEN ri.measurement = ${SQL_CONSTANTS.PACKAGE} AND p.packageWeight IS NOT NULL THEN p.fats * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = ${SQL_CONSTANTS.SERVING} AND p.servingWeight IS NOT NULL THEN p.fats * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) AS totalFats
    FROM RecipeEntity r
    JOIN RecipeIngredientEntity ri ON r.id = ri.recipeId
    JOIN ProductEntity p ON ri.productId = p.id
    GROUP BY r.id, r.name
    """
)
data class RecipeNutritionView(
    val recipeId: Long,
    val totalCalories: Float,
    val totalProteins: Float,
    val totalCarbohydrates: Float,
    val totalFats: Float
)
