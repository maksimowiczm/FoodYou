package com.maksimowiczm.foodyou.feature.addfood.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.ui.AddFoodScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.AddFoodSharedTransitionKeys
import com.maksimowiczm.foodyou.ui.LocalSharedTransitionScope
import kotlinx.serialization.Serializable

@Serializable
data class AddFoodFeature(
    val epochDay: Long,
    val meal: Meal,
    val productId: Long? = null
)

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.addFoodGraph(
    onClose: () -> Unit
) {
    composable<AddFoodFeature> {
        val (_, meal) = it.toRoute<AddFoodFeature>()

        val sharedTransitionScope =
            LocalSharedTransitionScope.current ?: error("No SharedTransitionScope found")

        with(sharedTransitionScope) {
            AddFoodScreen(
                onClose = onClose,
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            AddFoodSharedTransitionKeys.SearchScreen(
                                meal = meal
                            )
                        ),
                        animatedVisibilityScope = this@composable
                    )
                    .skipToLookaheadSize()
            )
        }
    }
}

fun NavController.navigateToAddFood(
    route: AddFoodFeature,
    navOptions: NavOptions? = null
) {
    navigate(route, navOptions)
}
