package com.maksimowiczm.foodyou.core.database.recipe

import androidx.room.DatabaseView
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.GRAM
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.PACKAGE
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.SERVING

@DatabaseView(
    """
    WITH TotalWeight AS (
        SELECT 
            r.id AS recipeId,
            SUM(
                CASE 
                    WHEN ri.measurement = $GRAM THEN ri.quantity
                    WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN ri.quantity * p.servingWeight
                    WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN ri.quantity * p.packageWeight
                    ELSE 0
                END
            ) AS totalWeight
        FROM RecipeEntity r
        JOIN RecipeIngredientEntity ri ON r.id = ri.recipeId
        JOIN ProductEntity p ON ri.productId = p.id
        GROUP BY r.id
    ) 
    SELECT
        r.id AS recipeId,
        tw.totalWeight AS totalWeight,
        tw.totalWeight / r.servings AS servingWeight
    FROM RecipeEntity r
    JOIN TotalWeight tw ON r.id = tw.recipeId
    """
)
data class RecipeWeightView(val recipeId: Long, val totalWeight: Float, val servingWeight: Float)
