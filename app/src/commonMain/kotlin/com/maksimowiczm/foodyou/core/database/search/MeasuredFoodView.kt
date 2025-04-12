package com.maksimowiczm.foodyou.core.database.search

import androidx.room.DatabaseView
import com.maksimowiczm.foodyou.core.database.measurement.Measurement

@DatabaseView(
    """
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
    """
)
data class MeasuredFoodView(
    // Identity
    val productId: Long?,
    val recipeId: Long?,
    val epochDay: Int,
    val mealId: Long,

    // Food
    val name: String,
    val brand: String?,
    val barcode: String?,
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val fats: Float,
    val packageWeight: Float?,
    val servingWeight: Float?,

    // Measurement
    val measurementId: Long,
    val measurement: Measurement,
    val quantity: Float
)
