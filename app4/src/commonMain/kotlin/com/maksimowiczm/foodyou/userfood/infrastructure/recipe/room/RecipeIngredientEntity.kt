package com.maksimowiczm.foodyou.userfood.infrastructure.recipe.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "RecipeIngredient",
    foreignKeys =
        [
            ForeignKey(
                entity = RecipeEntity::class,
                parentColumns = ["sqliteId"],
                childColumns = ["recipeSqliteId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index(value = ["recipeSqliteId"])],
)
internal data class RecipeIngredientEntity(
    @PrimaryKey(autoGenerate = true) val sqliteId: Long = 0,
    val recipeSqliteId: Long,
    val foodReferenceType: FoodReferenceType,
    val foodId: String,
    @Embedded(prefix = "quantity_") val quantity: RecipeQuantityEntity,
)
