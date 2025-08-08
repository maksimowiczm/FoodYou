package com.maksimowiczm.foodyou.navigation.graph.fooddiary

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.food.diary.search.ui.DiaryFoodSearchScreen
import com.maksimowiczm.foodyou.navigation.domain.FoodDiarySearchDestination
import com.maksimowiczm.foodyou.shared.navigation.forwardBackwardComposable

internal fun NavGraphBuilder.foodDiaryNavigationGraph(
    searchOnBack: () -> Unit,
    searchOnCreateRecipe: () -> Unit,
    searchOnCreateProduct: () -> Unit,
) {
    forwardBackwardComposable<FoodDiarySearchDestination> {
        val (epochDay, mealId) = it.toRoute<FoodDiarySearchDestination>()

        DiaryFoodSearchScreen(
            onBack = searchOnBack,
            onCreateRecipe = searchOnCreateRecipe,
            onCreateProduct = searchOnCreateProduct,
            epochDay = epochDay,
            mealId = mealId,
            animatedVisibilityScope = this,
        )
    }
}
