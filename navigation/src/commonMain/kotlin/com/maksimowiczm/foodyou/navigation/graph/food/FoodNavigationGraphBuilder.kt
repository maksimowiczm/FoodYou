package com.maksimowiczm.foodyou.navigation.graph.food

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.food.product.ui.UpdateProductScreen
import com.maksimowiczm.foodyou.navigation.domain.UpdateProductDestination
import com.maksimowiczm.foodyou.shared.navigation.forwardBackwardComposable

internal fun NavGraphBuilder.foodNavigationGraphBuilder(
    updateProductOnBack: () -> Unit,
    updateProductOnSave: () -> Unit,
) {
    forwardBackwardComposable<UpdateProductDestination> {
        val route = it.toRoute<UpdateProductDestination>()

        UpdateProductScreen(
            onBack = updateProductOnBack,
            onUpdate = updateProductOnSave,
            productId = route.foodId,
        )
    }
}
