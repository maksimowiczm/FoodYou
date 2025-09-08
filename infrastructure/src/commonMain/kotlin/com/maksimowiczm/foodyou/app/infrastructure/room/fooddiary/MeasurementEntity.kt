package com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.shared.domain.measurement.MeasurementType

@Entity(
    tableName = "Measurement",
    foreignKeys =
        [
            ForeignKey(
                entity = MealEntity::class,
                parentColumns = ["id"],
                childColumns = ["mealId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index(value = ["mealId"]), Index(value = ["epochDay"])],
)
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mealId: Long,
    val epochDay: Long,

    // Product or Recipe
    val productId: Long?,
    val recipeId: Long?,
    val measurement: MeasurementType,
    val quantity: Double,

    /** Epoch seconds */
    val createdAt: Long,
    /** Epoch seconds */
    val updatedAt: Long,
)
