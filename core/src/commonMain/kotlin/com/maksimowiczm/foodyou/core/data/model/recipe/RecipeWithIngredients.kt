package com.maksimowiczm.foodyou.core.data.model.recipe

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithIngredients(
    @Embedded
    val recipeEntity: RecipeEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "r_recipeId",
        entity = RecipeIngredientProductDetails::class
    )
    val ingredients: List<RecipeIngredientProductDetails>
)
