package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.ui.SettingsScreen
import com.maksimowiczm.foodyou.ui.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data object Settings

@Composable
fun FoodYouNavHost(
    homeFeatures: List<Feature.Home>,
    settingsFeatures: List<Feature.Settings>,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        crossfadeComposable<Home> {
            HomeScreen(
                animatedVisibilityScope = this,
                homeFeatures = homeFeatures.flatMap { it.buildHomeFeatures(navController) },
                onSettingsClick = {
                    navController.navigate(
                        route = Settings,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                }
            )
        }
        forwardBackwardComposable<Settings> {
            SettingsScreen(
                settingsFeatures = settingsFeatures.flatMap {
                    it.buildSettingsFeatures(navController)
                },
                onBack = {
                    navController.popBackStack(
                        route = Settings,
                        inclusive = true
                    )
                }
            )
        }
        homeFeatures.forEach { feature ->
            feature.run {
                homeGraph(navController)
            }
        }
        settingsFeatures.forEach { feature ->
            feature.run {
                settingsGraph(navController)
            }
        }
    }
}
