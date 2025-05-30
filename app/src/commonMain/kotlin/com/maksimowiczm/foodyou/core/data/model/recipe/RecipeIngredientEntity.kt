package com.maksimowiczm.foodyou.core.data.model.recipe

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.core.data.model.abstraction.EntityWithMeasurement
import com.maksimowiczm.foodyou.core.data.model.measurement.Measurement
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity

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
        ),
        ForeignKey(
            entity = RecipeIngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeIngredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["recipeId"]),
        Index(value = ["productId"]),
        Index(value = ["recipeIngredientId"])
    ]
)
data class RecipeIngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,

    // Ingredient
    val productId: Long?,
    val recipeIngredientId: Long?,

    override val measurement: Measurement,
    override val quantity: Float
) : EntityWithMeasurement
