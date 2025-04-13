package com.maksimowiczm.foodyou.core.database.measurement

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.core.database.core.EntityWithMeasurement
import com.maksimowiczm.foodyou.core.database.meal.MealEntity
import com.maksimowiczm.foodyou.core.database.recipe.RecipeEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
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
        Index(value = ["recipeId"]),
        Index(value = ["isDeleted"]),
        Index(value = ["mealId"])
    ]
)
data class RecipeMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Foreign keys
    val mealId: Long,
    val epochDay: Int,
    val recipeId: Long,

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
