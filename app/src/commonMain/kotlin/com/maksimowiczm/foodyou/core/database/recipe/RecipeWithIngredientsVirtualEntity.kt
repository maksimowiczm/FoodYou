package com.maksimowiczm.foodyou.core.database.recipe

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithIngredientsVirtualEntity(
    @Embedded
    val recipeEntity: RecipeEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        entity = RecipeIngredientEntity::class
    )
    val ingredients: List<RecipeIngredientEntity>
)
