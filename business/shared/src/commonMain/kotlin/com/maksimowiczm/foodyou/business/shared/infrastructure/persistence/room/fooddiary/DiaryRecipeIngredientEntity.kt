package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.MeasurementType

@Entity(
    tableName = "DiaryRecipeIngredient",
    foreignKeys =
        [
            ForeignKey(
                entity = DiaryRecipeEntity::class,
                parentColumns = ["id"],
                childColumns = ["recipeId"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = DiaryProductEntity::class,
                parentColumns = ["id"],
                childColumns = ["ingredientProductId"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = DiaryRecipeEntity::class,
                parentColumns = ["id"],
                childColumns = ["ingredientRecipeId"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
    indices =
        [
            Index(value = ["recipeId"]),
            Index(value = ["ingredientProductId"]),
            Index(value = ["ingredientRecipeId"]),
        ],
)
data class DiaryRecipeIngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: Long = 0,
    val ingredientProductId: Long?,
    val ingredientRecipeId: Long?,
    val measurement: MeasurementType,
    val quantity: Double,
)
