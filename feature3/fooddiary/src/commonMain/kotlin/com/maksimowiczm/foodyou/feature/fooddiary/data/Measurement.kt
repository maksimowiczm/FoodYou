package com.maksimowiczm.foodyou.feature.fooddiary.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.feature.food.data.Product
import com.maksimowiczm.foodyou.feature.food.data.Recipe
import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mealId"]),
        Index(value = ["epochDay"]),
        Index(value = ["productId"]),
        Index(value = ["recipeId"])
    ]
)
data class Measurement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mealId: Long,
    val epochDay: Long,

    // Product or Recipe
    val productId: Long?,
    val recipeId: Long?,

    val measurement: MeasurementType,
    val quantity: Float,

    /**
     * Epoch seconds
     */
    val createdAt: Long,
    val isDeleted: Boolean = false
)
