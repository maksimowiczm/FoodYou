package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.recipe.domain.CreateRecipeUseCase
import com.maksimowiczm.foodyou.feature.diary.recipe.domain.CreateRecipeUseCaseImpl
import com.maksimowiczm.foodyou.feature.diary.recipe.domain.IngredientMapper
import com.maksimowiczm.foodyou.feature.diary.recipe.domain.IngredientMapperImpl
import com.maksimowiczm.foodyou.feature.diary.recipe.domain.QueryIngredientsUseCase
import com.maksimowiczm.foodyou.feature.diary.recipe.domain.QueryIngredientsUseCaseImpl
import com.maksimowiczm.foodyou.feature.diary.recipe.domain.UpdateRecipeUseCase
import com.maksimowiczm.foodyou.feature.diary.recipe.domain.UpdateRecipeUseCaseImpl
import com.maksimowiczm.foodyou.feature.diary.recipe.ui.create.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.diary.recipe.ui.measure.MeasureIngredientViewModel
import com.maksimowiczm.foodyou.feature.diary.recipe.ui.search.IngredientsSearchViewModel
import com.maksimowiczm.foodyou.feature.diary.recipe.ui.update.UpdateRecipeViewModel
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
