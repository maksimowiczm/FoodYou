package com.maksimowiczm.foodyou.feature.recipe

import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.feature.recipe.ui.create.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.recipe.ui.measure.MeasureIngredientViewModel
import com.maksimowiczm.foodyou.feature.recipe.ui.search.IngredientsSearchViewModel
import com.maksimowiczm.foodyou.feature.recipe.ui.update.UpdateRecipeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val recipeModule = module {
//    factory {
//        QueryIngredientsUseCaseImpl(get(), get(), get(), get(), get())
//    }.bind<QueryIngredientsUseCase>()

//    factoryOf(::CreateRecipeUseCaseImpl).bind<CreateRecipeUseCase>()
//    factoryOf(::UpdateRecipeUseCaseImpl).bind<UpdateRecipeUseCase>()

//    factory { IngredientMapper() }

    viewModelOf(::CreateRecipeViewModel)
    viewModelOf(::IngredientsSearchViewModel)
    viewModel { (id: FoodId.Recipe?) ->
        IngredientsSearchViewModel(
            queryIngredientsUseCase = get(),
            recipeId = id
        )
    }
    viewModelOf(::MeasureIngredientViewModel)
    viewModelOf(::UpdateRecipeViewModel)
}
