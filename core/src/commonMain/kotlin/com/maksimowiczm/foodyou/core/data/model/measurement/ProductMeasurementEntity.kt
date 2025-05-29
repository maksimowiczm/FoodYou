package com.maksimowiczm.foodyou.core.data.model.measurement

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.core.data.model.abstraction.EntityWithMeasurement
import com.maksimowiczm.foodyou.core.data.model.meal.MealEntity
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["isDeleted"]),
        Index(value = ["mealId"]),
        Index(value = ["diaryEpochDay"])
    ]
)
data class ProductMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Foreign keys
    val mealId: Long,
    val diaryEpochDay: Int,
    val productId: Long,

    // Measurement
    override val measurement: Measurement,
    override val quantity: Float,

    // Additional
    /**
     * Epoch seconds
     */
    val createdAt: Long,
    val isDeleted: Boolean = false
) : EntityWithMeasurement
