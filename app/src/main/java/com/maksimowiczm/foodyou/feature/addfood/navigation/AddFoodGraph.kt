package com.maksimowiczm.foodyou.feature.addfood.navigation

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.ui.AddFoodScreen
import kotlinx.serialization.Serializable

@Serializable
data class AddFoodFeature(
    val epochDay: Long,
    val meal: Meal,
    val productId: Long? = null
)

fun NavGraphBuilder.addFoodGraph(
    onClose: () -> Unit
) {
    composable<AddFoodFeature>(
        enterTransition = {
            scaleIn(
                initialScale = .65f,
                animationSpec = tween(
                    durationMillis = 400,
                    easing = LinearOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 400,
                    easing = LinearOutSlowInEasing
                )
            )
        },
        exitTransition = {
            scaleOut(
                targetScale = .5f
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 100,
                    easing = FastOutLinearInEasing
                )
            )
        }
    ) {
        AddFoodScreen(
            onClose = onClose
        )
    }
}

fun NavController.navigateToAddFood(
    route: AddFoodFeature,
    navOptions: NavOptions? = null
) {
    navigate(route, navOptions)
}
