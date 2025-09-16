package com.maksimowiczm.foodyou.app.ui.food.recipe

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiFoodRecipeModule = module {
    viewModelOf(::CreateRecipeViewModel)
    viewModelOf(::UpdateRecipeViewModel)
    viewModelOf(::MeasureIngredientViewModel)
}
