package com.maksimowiczm.foodyou.app.ui.food.recipe

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

fun Module.foodRecipe() {
    viewModelOf(::CreateRecipeViewModel)
    viewModelOf(::UpdateRecipeViewModel)
    viewModelOf(::MeasureIngredientViewModel)
}
