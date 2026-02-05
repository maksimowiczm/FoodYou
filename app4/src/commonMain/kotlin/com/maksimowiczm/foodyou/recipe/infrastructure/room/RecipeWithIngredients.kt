package com.maksimowiczm.foodyou.recipe.infrastructure.room

import androidx.room.Embedded
import androidx.room.Relation

internal data class RecipeWithIngredients(
    @Embedded val recipe: RecipeEntity,
    @Relation(parentColumn = "sqliteId", entityColumn = "recipeSqliteId")
    val ingredients: List<RecipeIngredientEntity>,
)
