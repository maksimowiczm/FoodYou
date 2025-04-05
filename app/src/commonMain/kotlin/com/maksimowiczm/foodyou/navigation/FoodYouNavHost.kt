package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.ui.home.Home
import com.maksimowiczm.foodyou.ui.home.homeGraph
import com.maksimowiczm.foodyou.ui.settings.Settings
import com.maksimowiczm.foodyou.ui.settings.settingsGraph

@Composable
fun FoodYouNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        homeGraph(
            onSettings = {
                navController.navigate(Settings) {
                    launchSingleTop = true
                }
            }
        )
        settingsGraph(
            onBack = {
                navController.popBackStack<Settings>(inclusive = true)
            }
        )
    }
}
