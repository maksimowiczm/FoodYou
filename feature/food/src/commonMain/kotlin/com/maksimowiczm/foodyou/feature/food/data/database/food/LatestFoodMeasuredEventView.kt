package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.room.DatabaseView

@DatabaseView(
    """
    WITH RankedEvents AS (
        SELECT *,
               ROW_NUMBER() OVER (
                   PARTITION BY productId, recipeId
                   ORDER BY epochSeconds DESC
               ) AS rn
        FROM FoodEvent
        WHERE type = ${FoodEventTypeSQLConstants.MEASURED}
    )
    SELECT *
    FROM RankedEvents
    WHERE rn = 1
    """
)
data class LatestFoodMeasuredEventView(
    val id: Long,
    val type: FoodEventType,
    val epochSeconds: Long,
    val extra: String?,
    val productId: Long?,
    val recipeId: Long?
)
