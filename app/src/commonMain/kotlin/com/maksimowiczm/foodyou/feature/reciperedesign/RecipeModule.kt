package com.maksimowiczm.foodyou.feature.reciperedesign

import com.maksimowiczm.foodyou.feature.reciperedesign.ui.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.reciperedesign.ui.IngredientsSearchViewModel
import com.maksimowiczm.foodyou.feature.reciperedesign.ui.MeasureIngredientViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val recipeModule = module {
    viewModelOf(::CreateRecipeViewModel)
    viewModelOf(::IngredientsSearchViewModel)
    viewModelOf(::MeasureIngredientViewModel)
}
