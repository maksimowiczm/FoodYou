package com.maksimowiczm.foodyou.core.database.food

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithIngredients(
    @Embedded
    val recipeEntity: RecipeEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        entity = RecipeIngredientEntity::class
    )
    val ingredients: List<RecipeIngredientEntity>
)
