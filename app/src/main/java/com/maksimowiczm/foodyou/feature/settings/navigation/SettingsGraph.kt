package com.maksimowiczm.foodyou.feature.settings.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.maksimowiczm.foodyou.feature.settings.ui.fooddatabase.FoodDatabaseSettingsScreen
import com.maksimowiczm.foodyou.feature.settings.ui.goals.GoalsSettingsScreen
import com.maksimowiczm.foodyou.feature.settings.ui.home.SettingsHomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingsFeature

@Serializable
sealed interface SettingsRoute {

    @Serializable
    data object SettingsHome : SettingsRoute

    @Serializable
    data object FoodDatabaseSettings : SettingsRoute

    @Serializable
    data object GoalsSettings : SettingsRoute
}

fun NavGraphBuilder.settingsGraph(
    onBack: () -> Unit,
    onFoodDatabaseSettings: () -> Unit,
    onFoodDatabaseBack: () -> Unit,
    onGoalsSettings: () -> Unit,
    onGoalsBack: () -> Unit
) {
    navigation<SettingsFeature>(
        startDestination = SettingsRoute.SettingsHome
    ) {
        settingsComposable<SettingsRoute.SettingsHome> {
            SettingsHomeScreen(
                onBack = onBack,
                onFoodDatabaseClick = onFoodDatabaseSettings,
                onGoalsClick = onGoalsSettings,
                modifier = Modifier.fillMaxSize()
            )
        }
        settingsComposable<SettingsRoute.FoodDatabaseSettings> {
            FoodDatabaseSettingsScreen(
                onBack = onFoodDatabaseBack,
                modifier = Modifier.fillMaxSize()
            )
        }
        settingsComposable<SettingsRoute.GoalsSettings> {
            GoalsSettingsScreen(
                onBack = onGoalsBack,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private inline fun <reified R : SettingsRoute> NavGraphBuilder.settingsComposable(
    noinline content: @Composable () -> Unit
) {
    composable<R>(
        enterTransition = { slideIntoContainer(SlideDirection.Left) + fadeIn() },
        popEnterTransition = { slideIntoContainer(SlideDirection.Right) + fadeIn() },
        exitTransition = { slideOutOfContainer(SlideDirection.Left) + fadeOut() },
        popExitTransition = { slideOutOfContainer(SlideDirection.Right) + fadeOut() + scaleOut() }
    ) {
        content()
    }
}

fun NavController.navigateToSettings(
    navOptions: NavOptions? = null
) {
    navigate(SettingsFeature, navOptions)
}

fun <R : SettingsRoute> NavController.navigateToSettings(
    route: R,
    navOptions: NavOptions? = null
) = navigate(route, navOptions)
