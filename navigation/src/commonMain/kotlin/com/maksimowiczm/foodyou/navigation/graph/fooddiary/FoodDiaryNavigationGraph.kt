package com.maksimowiczm.foodyou.navigation.graph.fooddiary

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.food.diary.add.ui.AddEntryScreen
import com.maksimowiczm.foodyou.feature.food.diary.search.ui.DiaryFoodSearchScreen
import com.maksimowiczm.foodyou.navigation.domain.FoodDiaryAddDestination
import com.maksimowiczm.foodyou.navigation.domain.FoodDiarySearchDestination
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.navigation.forwardBackwardComposable
import kotlinx.datetime.LocalDate

internal fun NavGraphBuilder.foodDiaryNavigationGraph(
    searchOnBack: () -> Unit,
    searchOnCreateRecipe: () -> Unit,
    searchOnCreateProduct: () -> Unit,
    searchOnMeasure: (FoodId, Measurement, LocalDate, mealId: Long) -> Unit,
    addOnBack: () -> Unit,
) {
    forwardBackwardComposable<FoodDiarySearchDestination> {
        val route = it.toRoute<FoodDiarySearchDestination>()

        DiaryFoodSearchScreen(
            onBack = searchOnBack,
            onCreateRecipe = searchOnCreateRecipe,
            onCreateProduct = searchOnCreateProduct,
            onMeasure = { foodId, measurement ->
                searchOnMeasure(foodId, measurement, route.date, route.mealId)
            },
            date = route.date,
            mealId = route.mealId,
            animatedVisibilityScope = this,
        )
    }
    forwardBackwardComposable<FoodDiaryAddDestination> {
        val route = it.toRoute<FoodDiaryAddDestination>()

        AddEntryScreen(
            onBack = addOnBack,
            onEditFood = {
                // TODO
            },
            onEntryAdded = addOnBack,
            onFoodDeleted = addOnBack,
            foodId = route.foodId,
            mealId = route.mealId,
            date = route.date,
            measurement = route.measurement,
            animatedVisibilityScope = this,
        )
    }
}
