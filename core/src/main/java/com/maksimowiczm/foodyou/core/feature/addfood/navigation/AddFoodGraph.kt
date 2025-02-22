package com.maksimowiczm.foodyou.core.feature.addfood.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.maksimowiczm.foodyou.core.feature.addfood.ui.AddFoodScreen
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable

@Serializable
data class AddFoodFeature(val epochDay: Int, val mealId: Long, val productId: Long? = null)

fun NavGraphBuilder.addFoodGraph(onClose: () -> Unit, onSearchSettings: () -> Unit) {
    forwardBackwardComposable<AddFoodFeature> {
        AddFoodScreen(
            onClose = onClose,
            onSearchSettings = onSearchSettings
        )
    }
}

fun NavController.navigateToAddFood(route: AddFoodFeature, navOptions: NavOptions? = null) {
    navigate(route, navOptions)
}
