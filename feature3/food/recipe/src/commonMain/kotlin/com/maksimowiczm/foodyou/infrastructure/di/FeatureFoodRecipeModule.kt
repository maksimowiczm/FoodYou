package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.recipe.presentation.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.food.recipe.presentation.MeasureIngredientViewModel
import com.maksimowiczm.foodyou.feature.food.recipe.presentation.UpdateRecipeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureFoodRecipeModule = module {
    viewModelOf(::CreateRecipeViewModel)
    viewModelOf(::UpdateRecipeViewModel)
    viewModelOf(::MeasureIngredientViewModel)
}
