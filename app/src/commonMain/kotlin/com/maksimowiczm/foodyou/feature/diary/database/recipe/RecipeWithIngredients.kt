package com.maksimowiczm.foodyou.feature.diary.database.recipe

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithIngredients(
    @Embedded
    val recipe: RecipeEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        entity = RecipeIngredientEntity::class
    )
    val ingredients: List<RecipeIngredientEntity>
)
