package com.maksimowiczm.foodyou.navigation.graph.food

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.food.product.ui.UpdateProductScreen
import com.maksimowiczm.foodyou.feature.food.recipe.ui.UpdateRecipeScreen
import com.maksimowiczm.foodyou.navigation.domain.UpdateProductDestination
import com.maksimowiczm.foodyou.navigation.domain.UpdateRecipeDestination
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.navigation.forwardBackwardComposable

internal fun NavGraphBuilder.foodNavigationGraphBuilder(
    updateProductOnBack: () -> Unit,
    updateProductOnSave: () -> Unit,
    updateRecipeOnBack: () -> Unit,
    updateRecipeOnSave: () -> Unit,
    updateRecipeOnEditFood: (FoodId) -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
) {
    forwardBackwardComposable<UpdateProductDestination> {
        val route = it.toRoute<UpdateProductDestination>()

        UpdateProductScreen(
            onBack = updateProductOnBack,
            onUpdate = updateProductOnSave,
            productId = route.foodId,
        )
    }
    forwardBackwardComposable<UpdateRecipeDestination> {
        val route = it.toRoute<UpdateRecipeDestination>()

        UpdateRecipeScreen(
            onBack = updateRecipeOnBack,
            onUpdate = updateRecipeOnSave,
            onEditFood = updateRecipeOnEditFood,
            onUpdateUsdaApiKey = onUpdateUsdaApiKey,
            recipeId = route.foodId,
        )
    }
}
