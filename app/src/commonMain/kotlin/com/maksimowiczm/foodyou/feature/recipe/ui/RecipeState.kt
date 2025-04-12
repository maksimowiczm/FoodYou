package com.maksimowiczm.foodyou.feature.recipe.ui

import pro.respawn.kmmutils.inputforms.Input
import pro.respawn.kmmutils.inputforms.dsl.input

internal data class RecipeState(val name: Input = input(), val servings: Input = input("1"))
