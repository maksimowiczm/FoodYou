package com.maksimowiczm.foodyou.feature.recipe

import com.maksimowiczm.foodyou.feature.recipe.data.RecipeRepository
import com.maksimowiczm.foodyou.feature.recipe.ui.RecipeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val recipeModule = module {
    factoryOf(::RecipeRepository)

    viewModelOf(::RecipeViewModel)
}
