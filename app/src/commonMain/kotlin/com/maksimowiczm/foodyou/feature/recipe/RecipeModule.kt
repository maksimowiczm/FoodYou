package com.maksimowiczm.foodyou.feature.recipe

import com.maksimowiczm.foodyou.feature.recipe.domain.CreateRecipeUseCase
import com.maksimowiczm.foodyou.feature.recipe.domain.CreateRecipeUseCaseImpl
import com.maksimowiczm.foodyou.feature.recipe.domain.QueryIngredientsUseCase
import com.maksimowiczm.foodyou.feature.recipe.ui.create.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.recipe.ui.measure.MeasureIngredientViewModel
import com.maksimowiczm.foodyou.feature.recipe.ui.search.IngredientsSearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val recipeModule = module {
    factory {
        QueryIngredientsUseCase(get(), get(), get(), get(), get())
    }

    factory {
        CreateRecipeUseCaseImpl(get())
    }.bind<CreateRecipeUseCase>()

    viewModelOf(::CreateRecipeViewModel)
    viewModelOf(::IngredientsSearchViewModel)
    viewModelOf(::MeasureIngredientViewModel)
}
