package com.maksimowiczm.foodyou.feature.diary.database.recipe

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductEntity

@Entity(
    primaryKeys = ["recipeId", "productId"],
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
    val recipeId: Long,
    val productId: Long,
    val measurement: WeightMeasurementEnum,
    val quantity: Float
)
