package com.maksimowiczm.foodyou.food.infrastructure.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "FoodEvent",
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
    indices = [Index(value = ["productId"]), Index(value = ["recipeId"])],
)
data class FoodEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: FoodEventType,
    val epochSeconds: Long,
    val extra: String? = null,
    val productId: Long?,
    val recipeId: Long?,
)
