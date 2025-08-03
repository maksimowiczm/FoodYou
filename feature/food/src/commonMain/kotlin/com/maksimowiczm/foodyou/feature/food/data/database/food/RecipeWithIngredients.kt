package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithIngredients(
    @Embedded
    val recipeEntity: Recipe,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        entity = RecipeIngredient::class
    )
    val ingredients: List<RecipeIngredient>
)
