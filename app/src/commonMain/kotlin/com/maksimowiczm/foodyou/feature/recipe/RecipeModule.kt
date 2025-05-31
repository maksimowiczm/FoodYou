package com.maksimowiczm.foodyou.feature.recipe

import com.maksimowiczm.foodyou.feature.recipe.domain.CreateRecipeUseCase
import com.maksimowiczm.foodyou.feature.recipe.domain.CreateRecipeUseCaseImpl
import com.maksimowiczm.foodyou.feature.recipe.domain.IngredientMapper
import com.maksimowiczm.foodyou.feature.recipe.domain.QueryIngredientsUseCase
import com.maksimowiczm.foodyou.feature.recipe.domain.UpdateRecipeUseCase
import com.maksimowiczm.foodyou.feature.recipe.domain.UpdateRecipeUseCaseImpl
import com.maksimowiczm.foodyou.feature.recipe.ui.create.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.recipe.ui.measure.MeasureIngredientViewModel
import com.maksimowiczm.foodyou.feature.recipe.ui.search.IngredientsSearchViewModel
import com.maksimowiczm.foodyou.feature.recipe.ui.update.UpdateRecipeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val recipeModule = module {
    factory {
        QueryIngredientsUseCase(get(), get(), get(), get(), get())
    }

    factoryOf(::CreateRecipeUseCaseImpl).bind<CreateRecipeUseCase>()
    factoryOf(::UpdateRecipeUseCaseImpl).bind<UpdateRecipeUseCase>()

    factory { IngredientMapper() }

    viewModelOf(::CreateRecipeViewModel)
    viewModelOf(::IngredientsSearchViewModel)
    viewModelOf(::MeasureIngredientViewModel)
    viewModelOf(::UpdateRecipeViewModel)
}
