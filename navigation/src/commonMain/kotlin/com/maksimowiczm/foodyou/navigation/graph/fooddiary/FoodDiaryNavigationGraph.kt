package com.maksimowiczm.foodyou.navigation.graph.fooddiary

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.food.diary.add.ui.AddEntryScreen
import com.maksimowiczm.foodyou.feature.food.diary.search.ui.DiaryFoodSearchScreen
import com.maksimowiczm.foodyou.feature.food.product.ui.CreateProductScreen
import com.maksimowiczm.foodyou.feature.food.recipe.ui.CreateRecipeScreen
import com.maksimowiczm.foodyou.navigation.domain.FoodDiaryAddEntryDestination
import com.maksimowiczm.foodyou.navigation.domain.FoodDiaryCreateProductDestination
import com.maksimowiczm.foodyou.navigation.domain.FoodDiaryCreateRecipeDestination
import com.maksimowiczm.foodyou.navigation.domain.FoodDiarySearchDestination
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.navigation.forwardBackwardComposable
import kotlinx.datetime.LocalDate

internal fun NavGraphBuilder.foodDiaryNavigationGraph(
    searchOnBack: () -> Unit,
    searchOnCreateRecipe: (LocalDate, mealId: Long) -> Unit,
    searchOnCreateProduct: (LocalDate, mealId: Long) -> Unit,
    searchOnMeasure: (FoodId, Measurement, LocalDate, mealId: Long) -> Unit,
    addOnBack: () -> Unit,
    addOnEditFood: (FoodId) -> Unit,
    createProductOnBack: () -> Unit,
    createProductOnCreate: (FoodId, LocalDate, mealId: Long) -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
    createRecipeOnBack: () -> Unit,
    createRecipeOnCreate: (FoodId, LocalDate, mealId: Long) -> Unit,
    createOnEditFood: (FoodId) -> Unit,
) {
    forwardBackwardComposable<FoodDiarySearchDestination> {
        val route = it.toRoute<FoodDiarySearchDestination>()

        DiaryFoodSearchScreen(
            onBack = searchOnBack,
            onCreateRecipe = { searchOnCreateRecipe(route.date, route.mealId) },
            onCreateProduct = { searchOnCreateProduct(route.date, route.mealId) },
            onMeasure = { foodId, measurement ->
                searchOnMeasure(foodId, measurement, route.date, route.mealId)
            },
            onUpdateUsdaApiKey = onUpdateUsdaApiKey,
            date = route.date,
            mealId = route.mealId,
            animatedVisibilityScope = this,
        )
    }
    forwardBackwardComposable<FoodDiaryAddEntryDestination> {
        val route = it.toRoute<FoodDiaryAddEntryDestination>()

        AddEntryScreen(
            onBack = addOnBack,
            onEditFood = addOnEditFood,
            onEntryAdded = addOnBack,
            onFoodDeleted = addOnBack,
            foodId = route.foodId,
            mealId = route.mealId,
            date = route.date,
            measurement = route.measurement,
            animatedVisibilityScope = this,
        )
    }
    forwardBackwardComposable<FoodDiaryCreateProductDestination> {
        val route = it.toRoute<FoodDiaryCreateProductDestination>()

        CreateProductScreen(
            onBack = createProductOnBack,
            onCreate = { foodId -> createProductOnCreate(foodId, route.date, route.mealId) },
            onUpdateUsdaApiKey = onUpdateUsdaApiKey,
        )
    }
    forwardBackwardComposable<FoodDiaryCreateRecipeDestination> {
        val route = it.toRoute<FoodDiaryCreateRecipeDestination>()

        CreateRecipeScreen(
            onBack = createRecipeOnBack,
            onCreate = { foodId -> createRecipeOnCreate(foodId, route.date, route.mealId) },
            onEditFood = createOnEditFood,
            onUpdateUsdaApiKey = onUpdateUsdaApiKey,
        )
    }
}
