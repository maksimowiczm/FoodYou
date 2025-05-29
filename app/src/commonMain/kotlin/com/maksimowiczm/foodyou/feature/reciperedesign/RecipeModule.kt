package com.maksimowiczm.foodyou.feature.reciperedesign

import com.maksimowiczm.foodyou.feature.reciperedesign.domain.RecipeRepository
import com.maksimowiczm.foodyou.feature.reciperedesign.ui.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.reciperedesign.ui.measure.MeasureIngredientViewModel
import com.maksimowiczm.foodyou.feature.reciperedesign.ui.search.IngredientsSearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val recipeModule = module {
    factory {
        RecipeRepository(
            get()
        )
    }

    viewModelOf(::CreateRecipeViewModel)
    viewModelOf(::IngredientsSearchViewModel)
    viewModelOf(::MeasureIngredientViewModel)
}
