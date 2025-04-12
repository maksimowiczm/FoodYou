package com.maksimowiczm.foodyou.feature.recipe.ui

import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import pro.respawn.kmmutils.inputforms.Input
import pro.respawn.kmmutils.inputforms.dsl.input
import pro.respawn.kmmutils.inputforms.dsl.isEmpty
import pro.respawn.kmmutils.inputforms.dsl.isValid

internal data class RecipeState(
    val name: Input = input(),
    val servings: Input = input("1"),
    val ingredients: List<Ingredient> = emptyList()
) {
    val isModified: Boolean
        get() = !name.isEmpty || !servings.isEmpty || ingredients.isNotEmpty()

    val isValid: Boolean
        get() = name.isValid && servings.isValid && ingredients.isNotEmpty()
}
