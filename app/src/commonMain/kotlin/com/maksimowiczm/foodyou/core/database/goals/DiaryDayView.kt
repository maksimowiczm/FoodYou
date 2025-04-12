package com.maksimowiczm.foodyou.core.database.goals

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.database.core.NutrientsEmbedded
import com.maksimowiczm.foodyou.core.database.measurement.Measurement

@DatabaseView(
    """
    WITH Product AS (
        SELECT
            diaryEpochDay AS epochDay,
            pm.mealId AS mealId,
            m.name AS mealName,
            pm.productId AS productId,
            NULL AS recipeId,
            p.name AS foodName,
            p.calories AS calories,
            p.proteins AS proteins,
            p.carbohydrates AS carbohydrates,
            p.sugars AS sugars,
            p.fats AS fats,
            p.saturatedFats AS saturatedFats,
            p.salt AS salt,
            p.sodium AS sodium,
            p.fiber AS fiber,
            p.packageWeight AS packageWeight,
            p.servingWeight AS servingWeight,
            pm.id AS measurementId,
            pm.measurement AS measurement,
            pm.quantity AS quantity
        FROM ProductMeasurementEntity pm
        JOIN ProductEntity p ON pm.productId = p.id
        JOIN MealEntity m ON pm.mealId = m.id
        WHERE pm.isDeleted = 0
    ),
    Recipe AS (
        SELECT
            epochDay AS epochDay,
            rm.mealId AS mealId,
            m.name AS mealName,
            NULL AS productId,
            r.id AS recipeId,
            r.name AS foodName,
            rn.calories AS calories,
            rn.proteins AS proteins,
            rn.carbohydrates AS carbohydrates,
            rn.sugars AS sugars,
            rn.fats AS fats,
            rn.saturatedFats AS saturatedFats,
            rn.salt AS salt,
            rn.sodium AS sodium,
            rn.fiber AS fiber,
            rw.totalWeight AS packageWeight,
            rw.servingWeight AS servingWeight,
            rm.id AS measurementId,
            rm.measurement AS measurement,
            rm.quantity AS quantity
        FROM RecipeMeasurementEntity rm
        JOIN RecipeEntity r ON rm.recipeId = r.id
        JOIN RecipeNutritionView rn ON r.id = rn.recipeId
        JOIN RecipeWeightView rw ON r.id = rw.recipeId
        JOIN MealEntity m ON rm.mealId = m.id
        WHERE rm.isDeleted = 0
    )
    SELECT * FROM Product
    UNION ALL
    SELECT * FROM Recipe
    """
)
data class DiaryDayView(
    // Date
    val epochDay: Long,

    // Meal
    val mealId: Long,
    val mealName: String,

    // Food
    val productId: Long?,
    val recipeId: Long?,
    val foodName: String,
    @Embedded
    val nutrients: NutrientsEmbedded,
    val packageWeight: Float?,
    val servingWeight: Float?,

    // Measurement
    val measurementId: Long,
    val measurement: Measurement,
    val quantity: Float
)
