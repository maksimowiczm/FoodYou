package com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe

import androidx.room.Embedded
import androidx.room.Relation

internal data class RecipeWithIngredients(
    @Embedded val recipe: RecipeEntity,
    @Relation(parentColumn = "sqliteId", entityColumn = "recipeSqliteId")
    val ingredients: List<RecipeIngredientEntity>,
)
