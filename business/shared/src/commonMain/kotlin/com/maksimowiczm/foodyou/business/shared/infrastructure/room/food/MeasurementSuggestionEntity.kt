package com.maksimowiczm.foodyou.business.shared.infrastructure.room.food

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.business.shared.domain.measurement.MeasurementType

@Entity(
    tableName = "MeasurementSuggestion",
    foreignKeys =
        [
            ForeignKey(
                entity = ProductEntity::class,
                parentColumns = ["id"],
                childColumns = ["productId"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = RecipeEntity::class,
                parentColumns = ["id"],
                childColumns = ["recipeId"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
)
data class MeasurementSuggestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val productId: Long?,
    val recipeId: Long?,
    val type: MeasurementType,
    val value: Double,
    val epochSeconds: Long,
)
