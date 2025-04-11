package com.maksimowiczm.foodyou.core.database.goals

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.database.core.NutrientsEmbedded
import com.maksimowiczm.foodyou.core.database.measurement.Measurement

@DatabaseView(
    """
    SELECT
        diaryEpochDay AS epochDay,
        pm.mealId AS mealId,
        m.name AS mealName,
        pm.productId AS productId,
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
    """
)
data class DiaryDayView(
    // Date
    val epochDay: Long,

    // Meal
    val mealId: Long,
    val mealName: String,

    // Food
    val productId: Long,
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
