package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.shared.common.domain.measurement.MeasurementType

@Entity(tableName = "MeasurementSuggestion")
data class MeasurementSuggestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val productId: Long?,
    val recipeId: Long?,
    val type: MeasurementType,
    val value: Double,
    val epochSeconds: Long,
)
