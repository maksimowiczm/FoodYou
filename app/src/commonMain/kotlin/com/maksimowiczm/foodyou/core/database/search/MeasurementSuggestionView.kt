package com.maksimowiczm.foodyou.core.database.search

import androidx.room.DatabaseView
import com.maksimowiczm.foodyou.core.database.measurement.Measurement

@DatabaseView(
    """
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
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val fats: Float,
    val packageWeight: Float?,
    val servingWeight: Float?,

    // Measurement
    val measurement: Measurement?,
    val quantity: Float?
)
