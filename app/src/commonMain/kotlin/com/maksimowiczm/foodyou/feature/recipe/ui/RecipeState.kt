package com.maksimowiczm.foodyou.feature.recipe.ui

import com.maksimowiczm.foodyou.core.input.Input
import com.maksimowiczm.foodyou.core.input.Rule
import com.maksimowiczm.foodyou.core.input.dsl.checks
import com.maksimowiczm.foodyou.core.input.dsl.input
import com.maksimowiczm.foodyou.core.input.dsl.validates
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient

internal enum class RecipeAction {
    Create,
    Update
}

internal sealed interface RecipeInputError {
    object Empty : RecipeInputError
    object InvalidNumber : RecipeInputError
    object NotPositive : RecipeInputError
}

internal object RecipeInputRules {
    val NotEmpty = Rule<RecipeInputError> {
        { it.isNotBlank() } checks { RecipeInputError.Empty }
    }

    val PositiveInteger = Rule<RecipeInputError> {
        { it.toIntOrNull() } validates {
            when (it) {
                null -> RecipeInputError.InvalidNumber
                in 1..Int.MAX_VALUE -> null
                else -> RecipeInputError.NotPositive
            }
        }
    }
}

internal data class RecipeState(
    val name: Input<RecipeInputError> = input(),
    val servings: Input<RecipeInputError> = input("1"),
    val isModified: Boolean = false,
    val ingredients: List<Ingredient> = emptyList(),
    val action: RecipeAction
) {
    val isValid: Boolean
        get() = name.isValid && servings.isValid && ingredients.isNotEmpty()
}
