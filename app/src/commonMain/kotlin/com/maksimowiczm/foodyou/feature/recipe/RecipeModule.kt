package com.maksimowiczm.foodyou.feature.recipe

import com.maksimowiczm.foodyou.feature.recipe.ui.CreateRecipeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val recipeModule = module {
    viewModelOf(::CreateRecipeViewModel)
}
