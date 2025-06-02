package com.maksimowiczm.foodyou.core.database.food

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.core.database.diary.Measurement

//
// @Entity(
//    foreignKeys = [
//        ForeignKey(
//            entity = RecipeEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["recipeId"],
//            onDelete = ForeignKey.CASCADE
//        ),
//        ForeignKey(
//            entity = ProductEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["ingredientProductId"],
//            onDelete = ForeignKey.CASCADE
//        ),
//        ForeignKey(
//            entity = RecipeEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["ingredientRecipeId"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ],
//    indices = [
//        Index(value = ["recipeId"]),
//        Index(value = ["ingredientProductId"]),
//        Index(value = ["ingredientRecipeId"])
//    ]
// )
// data class RecipeIngredientEntity(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    val recipeId: Long = 0,
//
//    val ingredientProductId: Long?,
//    val ingredientRecipeId: Long?,
//
//    val measurementJson: String
// )

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
            childColumns = ["ingredientProductId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredientRecipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["recipeId"]),
        Index(value = ["ingredientProductId"]),
        Index(value = ["ingredientRecipeId"])
    ]
)
data class RecipeIngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long = 0,

    val ingredientProductId: Long?,
    val ingredientRecipeId: Long?,

    val measurement: Measurement,
    val quantity: Float
)
