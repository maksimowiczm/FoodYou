package com.maksimowiczm.foodyou.app.ui.food

import com.maksimowiczm.foodyou.app.ui.food.product.foodProduct
import com.maksimowiczm.foodyou.app.ui.food.recipe.foodRecipe
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchViewModel
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel

fun Module.food() {
    viewModel { (excluded: FoodId.Recipe?) ->
        FoodSearchViewModel(
            excludedRecipeId = excluded,
            searchHistoryRepository = get(),
            foodSearchRepository = get(),
            foodSearchUseCase = get(),
            dateProvider = get(),
        )
    }

    foodProduct()
    foodRecipe()
}
