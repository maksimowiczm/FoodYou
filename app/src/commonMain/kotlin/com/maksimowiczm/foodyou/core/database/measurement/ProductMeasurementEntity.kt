package com.maksimowiczm.foodyou.core.database.measurement

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.core.database.meal.MealEntity
import com.maksimowiczm.foodyou.core.database.product.ProductEntity

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
        Index(value = ["mealId"])
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
    val measurement: Measurement,
    val quantity: Float,

    // Additional
    /**
     * Epoch seconds
     */
    val createdAt: Long,
    val isDeleted: Boolean = false
)
