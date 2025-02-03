package com.maksimowiczm.foodyou.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.ui.SettingsScreen
import com.maksimowiczm.foodyou.core.ui.home.HomeScreen
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
        composable<Home> {
            HomeScreen(
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
        settingsComposable<Settings> {
            SettingsScreen(
                settingsFeatures = settingsFeatures.flatMap { it.buildSettingsFeatures(navController) },
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
