package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"]
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"]
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["recipeId"])
    ]
)
data class FoodEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: FoodEventType,
    val epochSeconds: Long,
    val extra: String? = null,
    val productId: Long?,
    val recipeId: Long?
)
