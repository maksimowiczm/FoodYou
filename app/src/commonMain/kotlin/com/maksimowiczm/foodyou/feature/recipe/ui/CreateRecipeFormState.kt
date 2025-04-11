package com.maksimowiczm.foodyou.feature.recipe.ui

import pro.respawn.kmmutils.inputforms.Input
import pro.respawn.kmmutils.inputforms.dsl.input

data class CreateRecipeFormState(
    override val name: Input = input(),
    override val servings: Input = input("1")
) : RecipeFormState
