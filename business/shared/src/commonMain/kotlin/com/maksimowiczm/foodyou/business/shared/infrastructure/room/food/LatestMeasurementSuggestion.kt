package com.maksimowiczm.foodyou.business.shared.infrastructure.room.food

import androidx.room.DatabaseView
import com.maksimowiczm.foodyou.core.shared.measurement.MeasurementType

@DatabaseView(
    """
        SELECT id, productId, recipeId, type, value, epochSeconds
        FROM (
            SELECT 
                ms.*,
                ROW_NUMBER() OVER (
                    PARTITION BY productId, recipeId
                    ORDER BY epochSeconds DESC
                ) AS rn
            FROM MeasurementSuggestion AS ms
        )
        WHERE rn = 1
    """
)
data class LatestMeasurementSuggestion(
    val productId: Long?,
    val recipeId: Long?,
    val type: MeasurementType,
    val value: Double,
    val epochSeconds: Long,
)
