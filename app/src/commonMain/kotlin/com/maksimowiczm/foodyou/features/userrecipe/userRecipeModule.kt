package com.maksimowiczm.foodyou.features.userrecipe

import com.maksimowiczm.foodyou.features.userrecipe.create.CreateRecipeViewModel
import com.maksimowiczm.foodyou.features.userrecipe.edit.EditRecipeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val userRecipeModule = module {
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
