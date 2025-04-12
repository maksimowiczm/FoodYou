package com.maksimowiczm.foodyou.feature.recipe

import com.maksimowiczm.foodyou.feature.recipe.ui.RecipeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val recipeModule = module {
    viewModelOf(::RecipeViewModel)
}
