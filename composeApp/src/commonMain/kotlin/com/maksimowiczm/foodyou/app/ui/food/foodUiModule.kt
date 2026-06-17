package com.maksimowiczm.foodyou.app.ui.food

import com.maksimowiczm.foodyou.app.ui.food.details.fooddatacentral.FoodDataCentralDetailsViewModel
import com.maksimowiczm.foodyou.app.ui.food.details.openfoodfacts.OpenFoodFactsDetailsViewModel
import com.maksimowiczm.foodyou.app.ui.food.details.userproduct.UserProductDetailsViewModel
import com.maksimowiczm.foodyou.app.ui.food.details.userrecipe.UserRecipeDetailsViewModel
import com.maksimowiczm.foodyou.app.ui.food.recipe.RecipeFormTransformer
import com.maksimowiczm.foodyou.app.ui.food.recipe.RecipeFormViewModel
import com.maksimowiczm.foodyou.app.ui.food.recipe.create.CreateRecipeViewModel
import com.maksimowiczm.foodyou.app.ui.food.recipe.edit.EditRecipeViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.SearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodSearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralSearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsSearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserFoodSearchExtension
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val foodUiModule = module {
    viewModelOf(::OpenFoodFactsDetailsViewModel)
    viewModelOf(::FoodDataCentralDetailsViewModel)
    viewModelOf(::UserProductDetailsViewModel)
    viewModelOf(::UserRecipeDetailsViewModel)

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

    viewModel { params ->
        SearchViewModel(
            initialQuery = params.getOrNull(),
            avoidCircularDependencyWith = params.getOrNull(),
            get(),
            get(),
            get(),
            get(),
        )
    }

    scope<SearchViewModel> {
        scoped {
            OpenFoodFactsSearchExtension(
                viewModel = get(),
                openFoodFactsService = get(),
                settingsRepository = get(),
            )
        }
        scoped {
            FoodDataCentralSearchExtension(
                viewModel = get(),
                foodDataCentralService = get(),
                settingsRepository = get(),
            )
        }
        scoped {
            UserFoodSearchExtension(
                viewModel = get(),
                repository = get(),
                compositionRepository = get(),
                foodNameSelector = get(),
            )
        }
        scoped {
            FavoriteFoodSearchExtension(
                viewModel = get(),
                appProfileManager = get(),
                foodDataCentralService = get(),
                openFoodFactsService = get(),
                userProductService = get(),
                userRecipeService = get(),
                nameSelector = get(),
            )
        }
    }
}
