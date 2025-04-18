package com.maksimowiczm.foodyou.core.data.database.recipe

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.data.database.measurement.MeasurementSQLConstants.GRAM
import com.maksimowiczm.foodyou.core.data.database.measurement.MeasurementSQLConstants.PACKAGE
import com.maksimowiczm.foodyou.core.data.database.measurement.MeasurementSQLConstants.SERVING
import com.maksimowiczm.foodyou.core.data.model.Nutrients

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
        ) * 100 / rw.totalWeight AS calories,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.proteins * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.proteins * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.proteins * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS proteins,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.carbohydrates * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.carbohydrates * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.carbohydrates * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS carbohydrates,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.sugars * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.sugars * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.sugars * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS sugars,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.fats * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.fats * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.fats * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS fats,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.saturatedFats * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.saturatedFats * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.saturatedFats * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS saturatedFats,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.salt * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.salt * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.salt * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS salt,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.sodium * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.sodium * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.sodium * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS sodium,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.fiber * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.fiber * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.fiber * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS fiber
    FROM RecipeEntity r
    JOIN RecipeIngredientEntity ri ON r.id = ri.recipeId
    JOIN ProductEntity p ON ri.productId = p.id
    JOIN RecipeWeightView rw ON r.id = rw.recipeId
    GROUP BY r.id
    """
)
data class RecipeNutritionView(
    val recipeId: Long,

    // Total nutrition per 100g
    @Embedded
    val nutrition: Nutrients
)
