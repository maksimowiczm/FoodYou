package com.maksimowiczm.foodyou.feature.recipe.ui

internal sealed interface CreateState {
    data object Nothing : CreateState
    data object CreatingRecipe : CreateState

    @JvmInline
    value class Created(val recipeId: Long) : CreateState
}
