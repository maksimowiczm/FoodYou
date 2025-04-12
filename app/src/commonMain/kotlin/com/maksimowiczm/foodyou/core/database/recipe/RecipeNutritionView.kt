package com.maksimowiczm.foodyou.core.database.recipe

import androidx.room.DatabaseView
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.GRAM
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.PACKAGE
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.SERVING

@DatabaseView(
    """
    SELECT 
        r.id AS recipeId,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.calories * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.calories * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.calories * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) AS totalCalories,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.proteins * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.proteins * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.proteins * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) AS totalProteins,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.carbohydrates * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.carbohydrates * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.carbohydrates * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) AS totalCarbohydrates,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.fats * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.fats * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.fats * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) AS totalFats
    FROM RecipeEntity r
    JOIN RecipeIngredientEntity ri ON r.id = ri.recipeId
    JOIN ProductEntity p ON ri.productId = p.id
    GROUP BY r.id
    """
)
data class RecipeNutritionView(
    val recipeId: Long,
    val totalCalories: Float,
    val totalProteins: Float,
    val totalCarbohydrates: Float,
    val totalFats: Float
)
