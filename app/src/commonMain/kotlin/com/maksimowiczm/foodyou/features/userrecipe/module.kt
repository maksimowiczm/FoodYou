package com.maksimowiczm.foodyou.features.userrecipe

import com.maksimowiczm.foodyou.features.userrecipe.create.CreateRecipeViewModel
import com.maksimowiczm.foodyou.features.userrecipe.edit.EditRecipeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf

internal fun Module.userRecipe() {
    viewModelOf(::CreateRecipeViewModel)
    viewModelOf(::EditRecipeViewModel)
    viewModel { params ->
        RecipeFormViewModel(
            initialIngredients = params.getOrNull() ?: emptyList(),
            fdc = get(),
            off = get(),
            up = get(),
            recipeService = get(),
            savedStateHandle = get(),
        )
    }
    factoryOf(::RecipeFormTransformer)
}
