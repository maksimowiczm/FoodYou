package com.maksimowiczm.foodyou.feature.recipe.ui

import pro.respawn.kmmutils.inputforms.Input
import pro.respawn.kmmutils.inputforms.dsl.input
import pro.respawn.kmmutils.inputforms.dsl.isEmpty
import pro.respawn.kmmutils.inputforms.dsl.isValid

internal data class RecipeState(val name: Input = input(), val servings: Input = input("1")) {
    val isModified: Boolean
        get() = !name.isEmpty || !servings.isEmpty

    val isValid: Boolean
        get() = name.isValid && servings.isValid
}
