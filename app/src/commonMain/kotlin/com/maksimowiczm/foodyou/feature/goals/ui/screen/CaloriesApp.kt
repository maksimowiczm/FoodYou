package com.maksimowiczm.foodyou.feature.goals.ui.screen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.CrossFadeComposableDefaults
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.product.UpdateProductScreen
import com.maksimowiczm.foodyou.feature.recipe.UpdateRecipe
import com.maksimowiczm.foodyou.feature.recipe.recipeGraph
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Composable
internal fun CaloriesApp(
    outerAnimatedScope: AnimatedContentScope,
    epochDay: Int,
    modifier: Modifier = Modifier
) {
    CaloriesNavHost(
        outerAnimatedScope = outerAnimatedScope,
        epochDay = epochDay,
        modifier = modifier
    )
}

@Composable
private fun CaloriesNavHost(
    outerAnimatedScope: AnimatedContentScope,
    epochDay: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val date = LocalDate.fromEpochDays(epochDay)

    NavHost(
        navController = navController,
        startDestination = CaloriesScreen,
        modifier = modifier
    ) {
        crossfadeComposable<CaloriesScreen>(
            popEnterTransition = {
                if (initialState.destination.hasRoute<UpdateRecipe>() == true) {
                    fadeIn(snap())
                } else {
                    CrossFadeComposableDefaults.enterTransition()
                }
            }
        ) {
            CaloriesScreen(
                date = date,
                animatedVisibilityScope = outerAnimatedScope,
                onFoodClick = {
                    when (it) {
                        is FoodId.Product -> navController.navigate(UpdateProduct(it.id)) {
                            launchSingleTop = true
                        }

                        is FoodId.Recipe -> navController.navigate(UpdateRecipe(it.id)) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
        forwardBackwardComposable<UpdateProduct> {
            val (productId) = it.toRoute<UpdateProduct>()

            UpdateProductScreen(
                productId = productId,
                onBack = {
                    navController.popBackStack<UpdateProduct>(inclusive = true)
                },
                onProductUpdate = {
                    navController.popBackStack<UpdateProduct>(inclusive = true)
                }
            )
        }
        recipeGraph(
            onCreateClose = {},
            onCreate = {},
            onUpdateClose = { navController.popBackStack<UpdateRecipe>(inclusive = true) },
            onUpdate = { navController.popBackStack<UpdateRecipe>(inclusive = true) }
        )
    }
}

@Serializable
private data object CaloriesScreen

@Serializable
private data class UpdateProduct(val id: Long)
