package com.maksimowiczm.foodyou.features.food

import com.maksimowiczm.foodyou.features.food.details.fooddatacentral.FoodDataCentralDetailsViewModel
import com.maksimowiczm.foodyou.features.food.details.openfoodfacts.OpenFoodFactsDetailsViewModel
import com.maksimowiczm.foodyou.features.food.details.userproduct.UserProductDetailsViewModel
import com.maksimowiczm.foodyou.features.food.details.userrecipe.UserRecipeDetailsViewModel
import com.maksimowiczm.foodyou.features.food.recipe.RecipeFormTransformer
import com.maksimowiczm.foodyou.features.food.recipe.RecipeFormViewModel
import com.maksimowiczm.foodyou.features.food.recipe.create.CreateRecipeViewModel
import com.maksimowiczm.foodyou.features.food.recipe.edit.EditRecipeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val foodUiModule = module {
    viewModelOf(::OpenFoodFactsDetailsViewModel)
    viewModelOf(::FoodDataCentralDetailsViewModel)
    viewModel { params ->
        UserProductDetailsViewModel(
            identity = params.get(),
            initialQuantity = params.getOrNull(),
            userProductService = get(),
            observeIsFavoriteFoodUseCase = get(),
            setFavoriteFoodUseCase = get(),
            savedStateHandle = get(),
        )
    }
    viewModel { params ->
        UserRecipeDetailsViewModel(
            identity = params.get(),
            initialQuantity = params.getOrNull(),
            userRecipeService = get(),
            observeIsFavoriteFoodUseCase = get(),
            setFavoriteFoodUseCase = get(),
            savedStateHandle = get(),
        )
    }
    viewModelOf(::CreateRecipeViewModel)
    viewModelOf(::EditRecipeViewModel)
    viewModel { params ->
        RecipeFormViewModel(
            initialIngredients = params.getOrNull() ?: emptyList(),
            fdc = get(),
            off = get(),
            up = get(),
            recipeService = get(),
            savedStateHandle = get(),
        )
    }
    factoryOf(::RecipeFormTransformer)
}
