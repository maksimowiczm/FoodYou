package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.feature.recipe.domain.CreateRecipeUseCase
import com.maksimowiczm.foodyou.feature.recipe.domain.CreateRecipeUseCaseImpl
import com.maksimowiczm.foodyou.feature.recipe.domain.IngredientMapper
import com.maksimowiczm.foodyou.feature.recipe.domain.IngredientMapperImpl
import com.maksimowiczm.foodyou.feature.recipe.domain.QueryIngredientsUseCase
import com.maksimowiczm.foodyou.feature.recipe.domain.QueryIngredientsUseCaseImpl
import com.maksimowiczm.foodyou.feature.recipe.domain.UpdateRecipeUseCase
import com.maksimowiczm.foodyou.feature.recipe.domain.UpdateRecipeUseCaseImpl
import com.maksimowiczm.foodyou.feature.recipe.ui.create.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.recipe.ui.measure.MeasureIngredientViewModel
import com.maksimowiczm.foodyou.feature.recipe.ui.search.IngredientsSearchViewModel
import com.maksimowiczm.foodyou.feature.recipe.ui.update.UpdateRecipeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val recipeModule = module {
    factoryOf(::QueryIngredientsUseCaseImpl).bind<QueryIngredientsUseCase>()

    factoryOf(::CreateRecipeUseCaseImpl).bind<CreateRecipeUseCase>()
    factoryOf(::UpdateRecipeUseCaseImpl).bind<UpdateRecipeUseCase>()

    factoryOf(::IngredientMapperImpl).bind<IngredientMapper>()

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
