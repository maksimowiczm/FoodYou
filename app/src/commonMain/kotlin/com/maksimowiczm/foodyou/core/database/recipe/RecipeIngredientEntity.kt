package com.maksimowiczm.foodyou.core.database.recipe

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.core.database.core.EntityWithMeasurement
import com.maksimowiczm.foodyou.core.database.measurement.Measurement
import com.maksimowiczm.foodyou.core.database.product.ProductEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["recipeId"]),
        Index(value = ["productId"])
    ]
)
data class RecipeIngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val productId: Long,
    override val measurement: Measurement,
    override val quantity: Float
) : EntityWithMeasurement
